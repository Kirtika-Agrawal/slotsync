package com.kirtika.slotsync.repository;

import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.SlotPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SlotPeriodRepository extends JpaRepository<SlotPeriod, Long> {

    List<SlotPeriod> findBySlotIn(Collection<Slot> slots);
}