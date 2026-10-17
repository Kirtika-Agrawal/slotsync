package com.kirtika.slotsync.dto;

import com.kirtika.slotsync.algorithm.Interval;

import java.time.DayOfWeek;
import java.util.List;

public record DaySchedule(DayOfWeek day, List<ClassBlock> classes, List<Interval> freeWindows) { }