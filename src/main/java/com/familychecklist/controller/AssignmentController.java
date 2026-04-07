package com.familychecklist.controller;

import com.familychecklist.model.*;
import com.familychecklist.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService svc;

    @GetMapping("/member/{memberId}")
    public List<Assignment> byMember(@PathVariable String memberId) {
        return svc.getByMember(memberId);
    }

    @GetMapping("/family/{familyId}")
    public List<Assignment> byFamily(@PathVariable String familyId) {
        return svc.getByFamily(familyId);
    }

    @PostMapping
    public Assignment create(@RequestBody Map<String, Object> body) {
        Assignment a = Assignment.builder()
            .memberId((String) body.get("memberId"))
            .memberName((String) body.get("memberName"))
            .memberEmail((String) body.getOrDefault("memberEmail", ""))
            .familyId((String) body.get("familyId"))
            .groupId((String) body.get("groupId"))
            .groupName((String) body.get("groupName"))
            .assignType((String) body.get("assignType"))
            .assignedDate((String) body.get("assignedDate"))
            .weekStart((String) body.get("weekStart"))
            .weekEnd((String) body.get("weekEnd"))
            .trackerGroupId((String) body.get("trackerGroupId"))
            .timeStart((String) body.get("timeStart"))
            .timeEnd((String) body.get("timeEnd"))
            .voiceEnabled(Boolean.TRUE.equals(body.get("voiceEnabled")))
            .alarmInterval(body.containsKey("alarmInterval") ? (Integer) body.get("alarmInterval") : 5)
            .build();

        @SuppressWarnings("unchecked")
        List<Map<String, String>> tasks = (List<Map<String, String>>) body.getOrDefault("tasks", List.of());
        return svc.create(a, tasks);
    }

    @PatchMapping("/{id}/state")
    public Assignment updateState(@PathVariable String id, @RequestBody Map<String, String> body) {
        return svc.updateState(id, body.get("state"));
    }

    @PatchMapping("/{id}")
    public Assignment update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return svc.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tracker-group/{trackerGroupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String trackerGroupId) {
        svc.deleteGroup(trackerGroupId);
        return ResponseEntity.noContent().build();
    }

    // Assignment tasks
    @GetMapping("/{id}/tasks")
    public List<AssignmentTask> getTasks(@PathVariable String id) {
        return svc.getTasks(id);
    }

    @PostMapping("/{id}/tasks")
    public AssignmentTask addTask(@PathVariable String id, @RequestBody Map<String, String> body) {
        return svc.addTask(id, body.get("taskName"));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        svc.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/tasks/{taskId}/toggle")
    public AssignmentTask toggleTask(@PathVariable String taskId, @RequestBody Map<String, Boolean> body) {
        return svc.toggleTask(taskId, body.get("completed"));
    }
}
