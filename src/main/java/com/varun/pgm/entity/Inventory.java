package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 100)
    private String itemName;

    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus = ConditionStatus.GOOD;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public enum ConditionStatus {
        GOOD, NEEDS_REPAIR, REPLACED
    }
}
