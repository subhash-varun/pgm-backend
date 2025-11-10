package com.varun.pgm.dto.request;

import com.varun.pgm.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    private LocalDate paymentDate;

    @Size(max = 20, message = "Payment month must be less than 20 characters")
    private String paymentMonth;

    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;

    @Size(max = 50, message = "Receipt number must be less than 50 characters")
    private String receiptNumber;

    private Payment.PaymentStatus status = Payment.PaymentStatus.PAID;
}
