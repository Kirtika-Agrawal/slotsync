package com.kirtika.slotsync.dto;

import java.util.List;

public record GroupFreeTimeResponse(
        Long groupId,
        String groupName,
        List<String> members,
        List<String> membersWithoutCourses,
        List<DayFreeTime> days
) { }