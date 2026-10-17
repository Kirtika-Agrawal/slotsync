package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Optional<Slot> findByName(String name);
}