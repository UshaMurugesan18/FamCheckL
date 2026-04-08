package com.familychecklist.repository;

import com.familychecklist.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByMemberIdOrderByCreatedAtDesc(String memberId);
    List<Assignment> findByFamilyIdOrderByCreatedAtDesc(String familyId);
    List<Assignment> findByTrackerGroupId(String trackerGroupId);

    // Find all active (assigned/snoozed) assignments for today that have a time window
    @Query("SELECT a FROM Assignment a WHERE a.state IN ('assigned','snoozed') AND a.timeStart IS NOT NULL AND a.timeEnd IS NOT NULL AND a.assignedDate = :today")
    List<Assignment> findActiveWithTimeWindow(String today);
}
