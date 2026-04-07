package com.familychecklist.repository;

import com.familychecklist.model.AssignmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask, String> {
    List<AssignmentTask> findByAssignmentId(String assignmentId);
    void deleteByAssignmentId(String assignmentId);
}
