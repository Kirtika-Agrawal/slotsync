package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.GroupMember;
import com.kirtika.slotsync.model.StudyGroup;
import com.kirtika.slotsync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByStudyGroupAndUser(StudyGroup studyGroup, User user);

    long countByStudyGroup(StudyGroup studyGroup);

    List<GroupMember> findByUserOrderByIdAsc(User user);

    List<GroupMember> findByStudyGroup(StudyGroup studyGroup);

    Optional<GroupMember> findByStudyGroupAndUser(StudyGroup studyGroup, User user);
}