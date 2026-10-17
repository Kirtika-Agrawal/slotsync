package com.kirtika.slotsync.dto;

import java.time.LocalTime;

public record ClassBlock(String courseCode, String slot, LocalTime start, LocalTime end) { }