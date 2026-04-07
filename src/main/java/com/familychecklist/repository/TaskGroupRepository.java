package com.familychecklist.repository;

import com.familychecklist.model.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, String> {
}
