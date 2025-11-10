package com.varun.pgm.dto.response;

import com.varun.pgm.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long tenantId;
    private String tenantName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMonth;
    private Payment.PaymentMethod paymentMethod;
    private String receiptNumber;
    private Payment.PaymentStatus status;
    private LocalDateTime createdAt;
}
