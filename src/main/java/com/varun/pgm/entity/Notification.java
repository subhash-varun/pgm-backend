package com.varun.pgm.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_target_user_read", columnList = "target_user_id, is_read"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "target_role", length = 50)
    private String targetRole;

    @Column(name = "target_property_id")
    private Long targetPropertyId;

    @Column(columnDefinition = "TEXT")
    private String payload; // JSON string for metadata

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean delivered = false;

    @Column(name = "delivery_attempts", nullable = false)
    private Integer deliveryAttempts = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        PAYMENT_REMINDER,
        MAINTENANCE_UPDATE,
        ROOM_AVAILABLE,
        TENANT_CHECK_IN,
        TENANT_CHECK_OUT,
        STAFF_ASSIGNMENT,
        INVENTORY_UPDATE,
        GENERAL_ANNOUNCEMENT,
        SYSTEM_ALERT
    }
}
