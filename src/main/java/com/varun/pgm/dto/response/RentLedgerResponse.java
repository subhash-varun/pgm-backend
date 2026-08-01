package com.varun.pgm.dto.response;

import com.varun.pgm.entity.RentLedger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentLedgerResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String roomNumber;
    private String billingMonth;
    private BigDecimal baseRent;
    private BigDecimal utilityCharges;
    private BigDecimal lateFee;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private BigDecimal advanceBalance;
    private LocalDate dueDate;
    private RentLedger.LedgerStatus status;
    private LocalDateTime createdAt;
}
