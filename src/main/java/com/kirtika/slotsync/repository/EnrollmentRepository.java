package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.Enrollment;
import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserAndSlot(User user, Slot slot);

    List<Enrollment> findByUserOrderByIdAsc(User user);

    Optional<Enrollment> findByIdAndUser(Long id, User user);
}