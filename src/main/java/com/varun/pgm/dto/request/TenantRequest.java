package com.varun.pgm.dto.request;

import com.varun.pgm.entity.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class TenantRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 15, message = "Phone must be less than 15 characters")
    private String phone;

    @Size(max = 50, message = "ID proof type must be less than 50 characters")
    private String idProofType;

    @Size(max = 100, message = "ID proof number must be less than 100 characters")
    private String idProofNumber;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal depositAmount = BigDecimal.ZERO;

    private Tenant.TenantStatus status = Tenant.TenantStatus.ACTIVE;
}
