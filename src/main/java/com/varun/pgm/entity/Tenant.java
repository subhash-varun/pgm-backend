package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 50)
    private String idProofType;

    @Column(length = 100)
    private String idProofNumber;

    @Column(nullable = false)
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    public enum TenantStatus {
        ACTIVE, MOVED_OUT
    }
}
