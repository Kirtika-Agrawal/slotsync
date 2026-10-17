package com.kirtika.slotsync.controller;

import com.kirtika.slotsync.dto.DaySchedule;
import com.kirtika.slotsync.service.TimetableService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping
    public List<DaySchedule> myWeek(@RequestParam(defaultValue = "60") int minMinutes,
                                    Authentication authentication) {
        return timetableService.getWeek(authentication.getName(), minMinutes);
    }
}