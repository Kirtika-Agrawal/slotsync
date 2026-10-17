package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long> {
}