package com.familychecklist.service;

import com.familychecklist.model.*;
import com.familychecklist.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepo;
    private final AssignmentTaskRepository taskRepo;
    private final SseService sseService;

    public List<Assignment> getByMember(String memberId) {
        return assignmentRepo.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public List<Assignment> getByFamily(String familyId) {
        return assignmentRepo.findByFamilyIdOrderByCreatedAtDesc(familyId);
    }

    @Transactional
    public Assignment create(Assignment assignment, List<Map<String, String>> tasks) {
        assignment.setState("assigned");
        assignment.setSnoozeCount(0);
        Assignment saved = assignmentRepo.save(assignment);
        for (Map<String, String> t : tasks) {
            taskRepo.save(AssignmentTask.builder()
                .assignmentId(saved.getId())
                .taskId(t.getOrDefault("taskId", ""))
                .taskName(t.get("taskName"))
                .completed(false)
                .build());
        }
        sseService.notifyFamily(saved.getFamilyId());
        sseService.notifyMember(saved.getMemberId());
        return saved;
    }

    public Assignment updateState(String id, String state) {
        Assignment a = assignmentRepo.findById(id).orElseThrow();
        a.setState(state);
        Assignment saved = assignmentRepo.save(a);
        sseService.notifyFamily(saved.getFamilyId());
        sseService.notifyMember(saved.getMemberId());
        return saved;
    }

    public Assignment update(String id, Map<String, Object> data) {
        Assignment a = assignmentRepo.findById(id).orElseThrow();
        if (data.containsKey("timeStart"))    a.setTimeStart((String) data.get("timeStart"));
        if (data.containsKey("timeEnd"))      a.setTimeEnd((String) data.get("timeEnd"));
        if (data.containsKey("alarmInterval")) a.setAlarmInterval((Integer) data.get("alarmInterval"));
        if (data.containsKey("voiceEnabled")) a.setVoiceEnabled((Boolean) data.get("voiceEnabled"));
        if (data.containsKey("state"))        a.setState((String) data.get("state"));
        if (data.containsKey("snoozeCount"))  a.setSnoozeCount((Integer) data.get("snoozeCount"));
        if (data.containsKey("photoUrl"))     a.setPhotoUrl((String) data.get("photoUrl"));
        if (data.containsKey("completedAt"))  a.setCompletedAt(LocalDateTime.now());
        Assignment saved = assignmentRepo.save(a);
        sseService.notifyFamily(saved.getFamilyId());
        sseService.notifyMember(saved.getMemberId());
        return saved;
    }

    @Transactional
    public void delete(String id) {
        Assignment a = assignmentRepo.findById(id).orElseThrow();
        taskRepo.deleteByAssignmentId(id);
        assignmentRepo.deleteById(id);
        sseService.notifyFamily(a.getFamilyId());
        sseService.notifyMember(a.getMemberId());
    }

    @Transactional
    public void deleteGroup(String trackerGroupId) {
        List<Assignment> group = assignmentRepo.findByTrackerGroupId(trackerGroupId);
        for (Assignment a : group) {
            taskRepo.deleteByAssignmentId(a.getId());
        }
        assignmentRepo.deleteAll(group);
        if (!group.isEmpty()) {
            sseService.notifyFamily(group.get(0).getFamilyId());
            sseService.notifyMember(group.get(0).getMemberId());
        }
    }

    public List<AssignmentTask> getTasks(String assignmentId) {
        return taskRepo.findByAssignmentId(assignmentId);
    }

    public AssignmentTask addTask(String assignmentId, String taskName) {
        return taskRepo.save(AssignmentTask.builder()
            .assignmentId(assignmentId)
            .taskName(taskName)
            .completed(false)
            .build());
    }

    public void deleteTask(String taskId) {
        taskRepo.deleteById(taskId);
    }

    public AssignmentTask toggleTask(String taskId, boolean completed) {
        AssignmentTask t = taskRepo.findById(taskId).orElseThrow();
        t.setCompleted(completed);
        return taskRepo.save(t);
    }
}
