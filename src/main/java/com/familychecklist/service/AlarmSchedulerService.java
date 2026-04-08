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
 * Runs every 1 minute.
 * For each active assignment inside its time window,
 * sends a push notification every alarmInterval minutes (2, 5, 10, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSchedulerService {

    private final AssignmentRepository assignmentRepo;
    private final WebPushService webPushService;

    @Scheduled(fixedDelay = 60 * 1000) // runs every 1 minute
    public void sendAlarms() {
        String today = LocalDate.now().toString();
        LocalTime now = LocalTime.now();
        int nowMins = now.getHour() * 60 + now.getMinute();

        List<Assignment> active = assignmentRepo.findActiveWithTimeWindow(today);

        for (Assignment a : active) {
            try {
                LocalTime start = LocalTime.parse(a.getTimeStart());
                LocalTime end   = LocalTime.parse(a.getTimeEnd());
                int startMins = start.getHour() * 60 + start.getMinute();
                int endMins   = end.getHour() * 60 + end.getMinute();

                // Outside time window — skip
                if (nowMins < startMins || nowMins > endMins) continue;

                // Fire at start, then every alarmInterval minutes
                int interval = (a.getAlarmInterval() != null && a.getAlarmInterval() > 0)
                    ? a.getAlarmInterval() : 5;
                int elapsed = nowMins - startMins;
                if (elapsed != 0 && elapsed % interval != 0) continue;

                webPushService.sendPush(
                    a.getMemberId(),
                    "🔔 Task Reminder: " + a.getGroupName(),
                    "Hi " + a.getMemberName() + "! Please complete your tasks. Tap to open.",
                    "/receiver/" + a.getMemberId()
                );
                log.info("Alarm sent to {} for '{}' (interval={}min)", a.getMemberName(), a.getGroupName(), interval);
            } catch (Exception e) {
                log.warn("Alarm failed for assignment {}: {}", a.getId(), e.getMessage());
            }
        }
    }
}
