package com.familychecklist.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String memberId;
    private String memberName;
    private String memberEmail;

    @Column(nullable = false)
    private String familyId;

    private String groupId;
    private String groupName;

    // "daily" | "weekly" | "tracker"
    private String assignType;

    private String assignedDate;   // YYYY-MM-DD
    private String weekStart;
    private String weekEnd;
    private String trackerGroupId;

    private String timeStart;      // HH:mm
    private String timeEnd;

    private boolean voiceEnabled;
    private int alarmInterval;

    // "assigned"|"snoozed"|"completed"|"approved"|"denied"
    @Column(nullable = false)
    private String state;

    private int snoozeCount;

    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (state == null) state = "assigned";
    }
}
