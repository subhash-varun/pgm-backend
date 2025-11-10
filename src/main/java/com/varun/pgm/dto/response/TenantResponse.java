package com.varun.pgm.dto.response;

import com.varun.pgm.entity.Tenant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private String name;
    private String email;
    private String phone;
    private String idProofType;
    private String idProofNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal depositAmount;
    private Tenant.TenantStatus status;
    private LocalDateTime createdAt;
}
