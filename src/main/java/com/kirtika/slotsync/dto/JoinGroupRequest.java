package com.kirtika.slotsync.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinGroupRequest(
        @NotBlank(message = "Invite code is required")
        String inviteCode
) { }