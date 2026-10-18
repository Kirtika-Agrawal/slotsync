package com.kirtika.slotsync.service;

import com.kirtika.slotsync.dto.CreateGroupRequest;
import com.kirtika.slotsync.dto.GroupResponse;
import com.kirtika.slotsync.dto.JoinGroupRequest;
import com.kirtika.slotsync.model.GroupMember;
import com.kirtika.slotsync.model.StudyGroup;
import com.kirtika.slotsync.model.User;
import com.kirtika.slotsync.repository.GroupMemberRepository;
import com.kirtika.slotsync.repository.StudyGroupRepository;
import com.kirtika.slotsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.List;

@Service
public class GroupService {

    // No 0, O, 1 or I, so codes are easy to read out loud
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupService(UserRepository userRepository,
                        StudyGroupRepository studyGroupRepository,
                        GroupMemberRepository groupMemberRepository) {
        this.userRepository = userRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public GroupResponse create(String email, CreateGroupRequest request) {
        User user = getUser(email);

        StudyGroup group = studyGroupRepository.save(
                new StudyGroup(request.name().trim(), generateUniqueCode(), user));
        groupMemberRepository.save(new GroupMember(group, user));

        return toResponse(group);
    }

    @Transactional
    public GroupResponse join(String email, JoinGroupRequest request) {
        User user = getUser(email);
        String code = request.inviteCode().trim().toUpperCase();

        StudyGroup group = studyGroupRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invalid invite code"));

        if (groupMemberRepository.existsByStudyGroupAndUser(group, user)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You are already in this group");
        }

        groupMemberRepository.save(new GroupMember(group, user));
        return toResponse(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> listForUser(String email) {
        User user = getUser(email);
        return groupMemberRepository.findByUserOrderByIdAsc(user).stream()
                .map(member -> toResponse(member.getStudyGroup()))
                .toList();
    }

    @Transactional
    public void leave(String email, Long groupId) {
        User user = getUser(email);

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group not found"));

        GroupMember membership = groupMemberRepository.findByStudyGroupAndUser(group, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group not found"));

        groupMemberRepository.delete(membership);
        groupMemberRepository.flush();

        // Nobody left? Remove the empty group too
        if (groupMemberRepository.countByStudyGroup(group) == 0) {
            studyGroupRepository.delete(group);
        }
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = randomCode();
        } while (studyGroupRepository.existsByInviteCode(code));
        return code;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private GroupResponse toResponse(StudyGroup group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getInviteCode(),
                groupMemberRepository.countByStudyGroup(group));
    }
}