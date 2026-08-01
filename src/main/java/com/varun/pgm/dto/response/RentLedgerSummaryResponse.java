package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentLedgerSummaryResponse {
    private String billingMonth;
    private BigDecimal totalBilled;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
    private long totalOverdueCount;
    private long totalTenantsCount;
}
