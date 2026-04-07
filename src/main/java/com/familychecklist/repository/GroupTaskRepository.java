package com.familychecklist.repository;

import com.familychecklist.model.GroupTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupTaskRepository extends JpaRepository<GroupTask, String> {
    List<GroupTask> findByGroupIdOrderByOrderIndexAsc(String groupId);
    void deleteByGroupId(String groupId);
}
