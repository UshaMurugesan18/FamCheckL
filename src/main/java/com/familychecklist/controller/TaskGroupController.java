package com.familychecklist.controller;

import com.familychecklist.model.*;
import com.familychecklist.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/task-groups")
@RequiredArgsConstructor
public class TaskGroupController {

    private final TaskGroupRepository groupRepo;
    private final GroupTaskRepository taskRepo;

    @GetMapping
    public List<TaskGroup> getAll() {
        return groupRepo.findAll();
    }

    @GetMapping("/{id}/tasks")
    public List<GroupTask> getTasks(@PathVariable String id) {
        return taskRepo.findByGroupIdOrderByOrderIndexAsc(id);
    }

    @PostMapping
    public TaskGroup create(@RequestBody Map<String, Object> body) {
        return groupRepo.save(TaskGroup.builder()
            .id(UUID.randomUUID().toString())
            .name((String) body.get("name"))
            .icon((String) body.getOrDefault("icon", "📋"))
            .build());
    }

    @PostMapping("/{id}/tasks")
    public GroupTask addTask(@PathVariable String id, @RequestBody Map<String, Object> body) {
        int order = taskRepo.findByGroupIdOrderByOrderIndexAsc(id).size();
        return taskRepo.save(GroupTask.builder()
            .groupId(id)
            .taskName((String) body.get("taskName"))
            .orderIndex(order)
            .build());
    }

    @DeleteMapping("/tasks/{taskId}")
    @Transactional
    public void deleteTask(@PathVariable String taskId) {
        taskRepo.deleteById(taskId);
    }
}
