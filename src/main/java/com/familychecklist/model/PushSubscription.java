package com.familychecklist.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "push_subscriptions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false, length = 512)
    private String endpoint;

    @Column(length = 256)
    private String p256dh;

    @Column(length = 256)
    private String auth;
}
