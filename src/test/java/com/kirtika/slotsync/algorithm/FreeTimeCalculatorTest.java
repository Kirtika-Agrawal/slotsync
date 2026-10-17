package com.kirtika.slotsync.algorithm;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FreeTimeCalculatorTest {

    private static Interval iv(String start, String end) {
        return new Interval(LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    void mergesOverlappingIntervals() {
        List<Interval> result = FreeTimeCalculator.merge(
                List.of(iv("09:00", "09:50"), iv("08:00", "08:50"), iv("09:40", "10:30")));

        assertEquals(List.of(iv("08:00", "08:50"), iv("09:00", "10:30")), result);
    }

    @Test
    void mergesPartialOverlapBetweenLabAndTheory() {
        // Lab L2 (08:51-09:40) overlaps theory F1 (09:00-09:50)
        List<Interval> result = FreeTimeCalculator.merge(
                List.of(iv("08:51", "09:40"), iv("09:00", "09:50")));

        assertEquals(List.of(iv("08:51", "09:50")), result);
    }

    @Test
    void findsGapsBetweenClasses() {
        List<Interval> busy = List.of(iv("08:00", "08:50"), iv("11:00", "11:50"));

        List<Interval> free = FreeTimeCalculator.findFreeWindows(
                busy, LocalTime.of(8, 0), LocalTime.of(14, 0), 60);

        assertEquals(List.of(iv("08:50", "11:00"), iv("11:50", "14:00")), free);
    }

    @Test
    void ignoresGapsShorterThanTheMinimum() {
        List<Interval> busy = List.of(iv("08:00", "08:50"), iv("09:00", "09:50"));

        List<Interval> free = FreeTimeCalculator.findFreeWindows(
                busy, LocalTime.of(8, 0), LocalTime.of(9, 50), 30);

        assertTrue(free.isEmpty());
    }

    @Test
    void wholeDayIsFreeWhenThereAreNoClasses() {
        List<Interval> free = FreeTimeCalculator.findFreeWindows(
                List.of(), LocalTime.of(8, 0), LocalTime.of(12, 0), 60);

        assertEquals(List.of(iv("08:00", "12:00")), free);
    }
}