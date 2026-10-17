package com.kirtika.slotsync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EnrollmentRequest(
        @NotBlank(message = "Course code is required")
        String courseCode,

        @NotBlank(message = "Course title is required")
        String courseTitle,

        @NotEmpty(message = "Add at least one slot")
        List<String> slots
) { }