package com.kirtika.slotsync.algorithm;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FreeTimeCalculator {

    private FreeTimeCalculator() {
        // utility class, no objects needed
    }

    /** Sorts the intervals and joins any that overlap or touch. */
    public static List<Interval> merge(List<Interval> intervals) {
        List<Interval> sorted = new ArrayList<>(intervals);
        sorted.sort(Comparator.comparing(Interval::start));

        List<Interval> merged = new ArrayList<>();
        for (Interval current : sorted) {
            if (merged.isEmpty()) {
                merged.add(current);
                continue;
            }

            Interval last = merged.get(merged.size() - 1);
            if (!current.start().isAfter(last.end())) {
                LocalTime newEnd = current.end().isAfter(last.end()) ? current.end() : last.end();
                merged.set(merged.size() - 1, new Interval(last.start(), newEnd));
            } else {
                merged.add(current);
            }
        }
        return merged;
    }

    /** Returns the gaps between busy blocks that are at least minMinutes long. */
    public static List<Interval> findFreeWindows(List<Interval> busy,
                                                 LocalTime dayStart,
                                                 LocalTime dayEnd,
                                                 int minMinutes) {
        if (minMinutes < 1) {
            throw new IllegalArgumentException("minMinutes must be at least 1");
        }

        List<Interval> free = new ArrayList<>();
        LocalTime cursor = dayStart;

        for (Interval block : merge(busy)) {
            if (block.start().isAfter(cursor)) {
                LocalTime gapEnd = block.start().isBefore(dayEnd) ? block.start() : dayEnd;
                addIfLongEnough(free, cursor, gapEnd, minMinutes);
            }
            if (block.end().isAfter(cursor)) {
                cursor = block.end();
            }
        }

        if (cursor.isBefore(dayEnd)) {
            addIfLongEnough(free, cursor, dayEnd, minMinutes);
        }
        return free;
    }

    private static void addIfLongEnough(List<Interval> free,
                                        LocalTime from,
                                        LocalTime to,
                                        int minMinutes) {
        if (Duration.between(from, to).toMinutes() >= minMinutes) {
            free.add(new Interval(from, to));
        }
    }
}