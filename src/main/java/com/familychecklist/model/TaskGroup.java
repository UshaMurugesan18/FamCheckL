package com.familychecklist.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_groups")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskGroup {
    @Id
    private String id;   // slug like "morning-preparation"

    @Column(nullable = false)
    private String name;

    private String icon;
    private Integer startHour;
    private Integer endHour;

    // Store days as comma-separated e.g. "1,2,3,4,5,6"
    private String days;
}
