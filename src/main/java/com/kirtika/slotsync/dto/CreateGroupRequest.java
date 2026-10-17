package com.kirtika.slotsync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "Group name is required")
        @Size(max = 100, message = "Group name must be 100 characters or fewer")
        String name
) { }