package com.familychecklist.repository;

import com.familychecklist.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByMemberIdOrderByCreatedAtDesc(String memberId);
    List<Assignment> findByFamilyIdOrderByCreatedAtDesc(String familyId);
    List<Assignment> findByTrackerGroupId(String trackerGroupId);
}
