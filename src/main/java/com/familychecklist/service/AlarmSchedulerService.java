package com.familychecklist.service;

import com.familychecklist.model.Assignment;
import com.familychecklist.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Fires every 5 minutes.
 * For each active assignment whose time window includes NOW,
 * sends a Web Push notification to the receiver's device.
 * The OS shows it on the lock screen — tapping opens the app and triggers voice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSchedulerService {

    private final AssignmentRepository assignmentRepo;
    private final WebPushService webPushService;

    @Scheduled(fixedDelay = 5 * 60 * 1000) // every 5 minutes
    public void sendAlarms() {
        String today = LocalDate.now().toString(); // YYYY-MM-DD
        LocalTime now = LocalTime.now();

        List<Assignment> active = assignmentRepo.findActiveWithTimeWindow(today);

        for (Assignment a : active) {
            try {
                LocalTime start = LocalTime.parse(a.getTimeStart());
                LocalTime end   = LocalTime.parse(a.getTimeEnd());
                if (now.isBefore(start) || now.isAfter(end)) continue; // outside window

                webPushService.sendPush(
                    a.getMemberId(),
                    "🔔 Task Reminder: " + a.getGroupName(),
                    "Hi " + a.getMemberName() + "! Please complete your tasks now. Tap to open.",
                    "/receiver/" + a.getMemberId()
                );
                log.info("Alarm sent to member {} for assignment {}", a.getMemberId(), a.getId());
            } catch (Exception e) {
                log.warn("Alarm failed for assignment {}: {}", a.getId(), e.getMessage());
            }
        }
    }
}
