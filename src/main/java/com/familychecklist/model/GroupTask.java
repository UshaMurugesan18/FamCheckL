package com.familychecklist.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_tasks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String groupId;

    @Column(nullable = false)
    private String taskName;

    private int orderIndex;
}
