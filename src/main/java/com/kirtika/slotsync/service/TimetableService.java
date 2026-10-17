package com.kirtika.slotsync.service;

import com.kirtika.slotsync.algorithm.FreeTimeCalculator;
import com.kirtika.slotsync.algorithm.Interval;
import com.kirtika.slotsync.dto.ClassBlock;
import com.kirtika.slotsync.dto.DaySchedule;
import com.kirtika.slotsync.model.Enrollment;
import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.SlotPeriod;
import com.kirtika.slotsync.model.User;
import com.kirtika.slotsync.repository.EnrollmentRepository;
import com.kirtika.slotsync.repository.SlotPeriodRepository;
import com.kirtika.slotsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimetableService {

    private static final LocalTime DAY_START = LocalTime.of(8, 0);
    private static final LocalTime DAY_END = LocalTime.of(20, 0);
    private static final List<DayOfWeek> WEEKDAYS = List.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SlotPeriodRepository slotPeriodRepository;

    public TimetableService(UserRepository userRepository,
                            EnrollmentRepository enrollmentRepository,
                            SlotPeriodRepository slotPeriodRepository) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.slotPeriodRepository = slotPeriodRepository;
    }

    @Transactional(readOnly = true)
    public List<DaySchedule> getWeek(String email, int minMinutes) {
        if (minMinutes < 1 || minMinutes > 600) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "minMinutes must be between 1 and 600");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<Enrollment> enrollments = enrollmentRepository.findByUserOrderByIdAsc(user);
        Map<DayOfWeek, List<ClassBlock>> classesByDay = new EnumMap<>(DayOfWeek.class);

        if (!enrollments.isEmpty()) {
            Map<Long, Enrollment> bySlotId = new HashMap<>();
            for (Enrollment e : enrollments) {
                bySlotId.put(e.getSlot().getId(), e);
            }

            List<Slot> slots = enrollments.stream().map(Enrollment::getSlot).toList();
            for (SlotPeriod period : slotPeriodRepository.findBySlotIn(slots)) {
                Enrollment enrollment = bySlotId.get(period.getSlot().getId());
                classesByDay
                        .computeIfAbsent(period.getDayOfWeek(), d -> new ArrayList<>())
                        .add(new ClassBlock(
                                enrollment.getCourse().getCode(),
                                period.getSlot().getName(),
                                period.getStartTime(),
                                period.getEndTime()));
            }
        }

        List<DaySchedule> week = new ArrayList<>();
        for (DayOfWeek day : WEEKDAYS) {
            List<ClassBlock> classes = new ArrayList<>(classesByDay.getOrDefault(day, List.of()));
            classes.sort(Comparator.comparing(ClassBlock::start));

            List<Interval> busy = classes.stream()
                    .map(c -> new Interval(c.start(), c.end()))
                    .toList();

            List<Interval> free = FreeTimeCalculator.findFreeWindows(
                    busy, DAY_START, DAY_END, minMinutes);

            week.add(new DaySchedule(day, classes, free));
        }
        return week;
    }
}