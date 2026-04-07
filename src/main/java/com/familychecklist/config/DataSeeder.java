package com.familychecklist.config;

import com.familychecklist.model.*;
import com.familychecklist.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TaskGroupRepository groupRepo;
    private final GroupTaskRepository taskRepo;

    @Override
    public void run(String... args) {
        if (groupRepo.count() > 0) return; // already seeded

        log.info("Seeding task groups...");
        seed("morning-preparation", "Morning Preparation", "🌅", 6, 8, "1,2,3,4,5,6",
            new String[]{"Dress", "Shoes", "Socks", "Pen", "Glasses", "ID Card", "Belt"});

        seed("evening-routine", "Evening Routine", "🌇", 17, 19, "1,2,3,4,5,6",
            new String[]{"Homework", "Bag Pack", "Uniform", "Water Bottle", "Snack Box", "Diary Sign"});

        seed("bedtime", "Bedtime", "🌙", 21, 22, "0,1,2,3,4,5,6",
            new String[]{"Brush Teeth", "Wash Face", "Change Clothes", "Set Alarm", "Lights Off"});

        seed("homework", "Homework", "📚", 16, 18, "1,2,3,4,5",
            new String[]{"Math", "English", "Science", "Social Studies", "Drawing", "Reading"});

        log.info("Seeding complete.");
    }

    private void seed(String id, String name, String icon, int startHour, int endHour, String days, String[] tasks) {
        if (groupRepo.existsById(id)) return;
        groupRepo.save(TaskGroup.builder()
            .id(id).name(name).icon(icon)
            .startHour(startHour).endHour(endHour)
            .days(days)
            .build());
        for (int i = 0; i < tasks.length; i++) {
            taskRepo.save(GroupTask.builder()
                .groupId(id).taskName(tasks[i]).orderIndex(i)
                .build());
        }
    }
}
