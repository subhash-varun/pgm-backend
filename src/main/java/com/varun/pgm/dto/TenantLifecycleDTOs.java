package com.varun.pgm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TenantLifecycleDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitiateNoticeRequest {
        private LocalDate noticeDate;
        private LocalDate expectedExitDate;
        private String exitReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SettlementSummaryResponse {
        private Long tenantId;
        private String tenantName;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate noticeDate;
        private LocalDate expectedExitDate;
        private BigDecimal securityDepositPaid;
        private BigDecimal advanceBalance;
        private BigDecimal outstandingRentDues;
        private Long unpaidLedgerCount;
        private BigDecimal suggestedDamageCharges;
        private BigDecimal estimatedNetRefund; // deposit + advance - rentDues
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteExitRequest {
        private LocalDate actualExitDate;
        private BigDecimal damageCharges;
        private BigDecimal otherDeductions;
        private String paymentMethod; // UPI, CASH, BANK_TRANSFER
        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SettlementResponse {
        private Long settlementId;
        private Long tenantId;
        private String tenantName;
        private LocalDate noticeDate;
        private LocalDate actualExitDate;
        private BigDecimal securityDepositPaid;
        private BigDecimal outstandingRentDues;
        private BigDecimal damageCharges;
        private BigDecimal otherDeductions;
        private BigDecimal netRefundAmount;
        private String paymentMethod;
        private String remarks;
        private String status;
        private LocalDateTime settledAt;
    }
}
