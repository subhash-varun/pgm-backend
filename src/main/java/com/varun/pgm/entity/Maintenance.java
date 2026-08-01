package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private Tenant reportedBy;

    @Column(nullable = false, length = 200)
    private String issue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "image_url")
    private String imageUrl;

    public enum MaintenanceStatus {
        PENDING, IN_PROGRESS, RESOLVED, CANCELLED
    }

    public enum MaintenancePriority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
