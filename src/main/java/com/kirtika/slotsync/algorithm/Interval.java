package com.kirtika.slotsync.algorithm;

import java.time.LocalTime;

public record Interval(LocalTime start, LocalTime end) {

    public Interval {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
    }
}