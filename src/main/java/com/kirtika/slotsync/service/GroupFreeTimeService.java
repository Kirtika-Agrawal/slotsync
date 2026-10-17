package com.kirtika.slotsync.service;

import com.kirtika.slotsync.algorithm.FreeTimeCalculator;
import com.kirtika.slotsync.algorithm.Interval;
import com.kirtika.slotsync.dto.DayFreeTime;
import com.kirtika.slotsync.dto.GroupFreeTimeResponse;
import com.kirtika.slotsync.model.Enrollment;
import com.kirtika.slotsync.model.GroupMember;
import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.SlotPeriod;
import com.kirtika.slotsync.model.StudyGroup;
import com.kirtika.slotsync.model.User;
import com.kirtika.slotsync.repository.EnrollmentRepository;
import com.kirtika.slotsync.repository.GroupMemberRepository;
import com.kirtika.slotsync.repository.SlotPeriodRepository;
import com.kirtika.slotsync.repository.StudyGroupRepository;
import com.kirtika.slotsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupFreeTimeService {

    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SlotPeriodRepository slotPeriodRepository;

    public GroupFreeTimeService(UserRepository userRepository,
                                StudyGroupRepository studyGroupRepository,
                                GroupMemberRepository groupMemberRepository,
                                EnrollmentRepository enrollmentRepository,
                                SlotPeriodRepository slotPeriodRepository) {
        this.userRepository = userRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.slotPeriodRepository = slotPeriodRepository;
    }

    @Transactional(readOnly = true)
    public GroupFreeTimeResponse getFreeTime(String email, Long groupId, int minMinutes) {
        if (minMinutes < 1 || minMinutes > 600) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "minMinutes must be between 1 and 600");
        }

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Same answer for "no such group" and "not your group", so nothing leaks
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group not found"));
        if (!groupMemberRepository.existsByStudyGroupAndUser(group, requester)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        List<User> users = groupMemberRepository.findByStudyGroup(group).stream()
                .map(GroupMember::getUser)
                .toList();

        List<Enrollment> enrollments = enrollmentRepository.findByUserIn(users);

        Set<Long> usersWithCourses = enrollments.stream()
                .map(e -> e.getUser().getId())
                .collect(Collectors.toSet());
        List<String> membersWithoutCourses = users.stream()
                .filter(u -> !usersWithCourses.contains(u.getId()))
                .map(User::getName)
                .toList();

        // The group is busy whenever ANY member is busy, so we just pool every slot
        List<Slot> slots = enrollments.stream().map(Enrollment::getSlot).toList();

        Map<DayOfWeek, List<Interval>> busyByDay = new EnumMap<>(DayOfWeek.class);
        if (!slots.isEmpty()) {
            for (SlotPeriod period : slotPeriodRepository.findBySlotIn(slots)) {
                busyByDay
                        .computeIfAbsent(period.getDayOfWeek(), d -> new ArrayList<>())
                        .add(new Interval(period.getStartTime(), period.getEndTime()));
            }
        }

        List<DayFreeTime> days = new ArrayList<>();
        for (DayOfWeek day : TimetableService.WEEKDAYS) {
            List<Interval> free = FreeTimeCalculator.findFreeWindows(
                    busyByDay.getOrDefault(day, List.of()),
                    TimetableService.DAY_START,
                    TimetableService.DAY_END,
                    minMinutes);
            days.add(new DayFreeTime(day, free));
        }

        return new GroupFreeTimeResponse(
                group.getId(),
                group.getName(),
                users.stream().map(User::getName).toList(),
                membersWithoutCourses,
                days);
    }
}