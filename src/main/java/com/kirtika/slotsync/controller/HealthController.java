package com.kirtika.slotsync.controller;

import com.kirtika.slotsync.repository.SlotRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final SlotRepository slotRepository;

    public HealthController(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        slotRepository.count(); // makes a real database call
        return Map.of("status", "ok");
    }
}