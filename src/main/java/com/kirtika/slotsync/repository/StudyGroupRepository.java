package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    Optional<StudyGroup> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}