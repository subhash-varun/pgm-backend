package com.varun.pgm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id")
    private RentLedger ledger;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private LocalDate paymentDate;

    @Column(length = 20)
    private String paymentMonth;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(unique = true, length = 50)
    private String receiptNumber;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PAID;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum PaymentMethod {
        CASH, ONLINE, BANK_TRANSFER, UPI
    }

    public enum PaymentStatus {
        PAID, PENDING, FAILED
    }
}
