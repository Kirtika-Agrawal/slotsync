package com.kirtika.slotsync.config;

import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.SlotPeriod;
import com.kirtika.slotsync.model.SlotType;
import com.kirtika.slotsync.repository.SlotPeriodRepository;
import com.kirtika.slotsync.repository.SlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SlotDataLoader implements CommandLineRunner {

    private final SlotRepository slotRepository;
    private final SlotPeriodRepository slotPeriodRepository;

    public SlotDataLoader(SlotRepository slotRepository,
                          SlotPeriodRepository slotPeriodRepository) {
        this.slotRepository = slotRepository;
        this.slotPeriodRepository = slotPeriodRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (slotPeriodRepository.count() > 0) {
            System.out.println("Slot data already loaded, skipping.");
            return;
        }

        Map<String, DayOfWeek> days = Map.of(
                "MON", DayOfWeek.MONDAY,
                "TUE", DayOfWeek.TUESDAY,
                "WED", DayOfWeek.WEDNESDAY,
                "THU", DayOfWeek.THURSDAY,
                "FRI", DayOfWeek.FRIDAY);

        Map<String, Slot> slotsByName = new HashMap<>();
        int periodCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("slot_periods.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            reader.readLine(); // skip the header row
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                DayOfWeek day = days.get(parts[0].trim());
                String slotName = parts[1].trim();
                SlotType type = SlotType.valueOf(parts[2].trim());
                LocalTime start = LocalTime.parse(parts[3].trim());
                LocalTime end = LocalTime.parse(parts[4].trim());

                Slot slot = slotsByName.get(slotName);
                if (slot == null) {
                    slot = slotRepository.save(new Slot(slotName, type));
                    slotsByName.put(slotName, slot);
                }
                slotPeriodRepository.save(new SlotPeriod(slot, day, start, end));
                periodCount++;
            }
        }

        System.out.println("Loaded " + periodCount + " slot periods and "
                + slotsByName.size() + " slots.");
    }
}