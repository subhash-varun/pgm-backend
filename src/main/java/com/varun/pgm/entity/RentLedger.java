package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_ledger", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "billing_month"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "billing_month", nullable = false, length = 7)
    private String billingMonth; // YYYY-MM

    @Column(name = "base_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRent;

    @Column(name = "utility_charges", precision = 10, scale = 2)
    private BigDecimal utilityCharges = BigDecimal.ZERO;

    @Column(name = "late_fee", precision = 10, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance_due", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceDue;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private LedgerStatus status = LedgerStatus.UNPAID;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum LedgerStatus {
        UNPAID, PARTIAL, PAID, OVERDUE
    }
}
