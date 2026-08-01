package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_settlement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "notice_date")
    private LocalDate noticeDate;

    @Column(name = "actual_exit_date", nullable = false)
    private LocalDate actualExitDate;

    @Column(name = "security_deposit_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal securityDepositPaid = BigDecimal.ZERO;

    @Column(name = "outstanding_rent_dues", nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingRentDues = BigDecimal.ZERO;

    @Column(name = "damage_charges", nullable = false, precision = 10, scale = 2)
    private BigDecimal damageCharges = BigDecimal.ZERO;

    @Column(name = "other_deductions", nullable = false, precision = 10, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "net_refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal netRefundAmount = BigDecimal.ZERO;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.SETTLED;

    @Column(name = "settled_at")
    private LocalDateTime settledAt = LocalDateTime.now();

    public enum SettlementStatus {
        PENDING, SETTLED
    }
}
