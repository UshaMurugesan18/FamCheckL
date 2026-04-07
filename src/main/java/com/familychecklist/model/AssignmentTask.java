package com.familychecklist.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignment_tasks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssignmentTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String assignmentId;

    private String taskId;

    @Column(nullable = false)
    private String taskName;

    private boolean completed;
}
