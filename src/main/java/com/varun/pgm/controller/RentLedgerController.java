package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.PayRentRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.PaymentResponse;
import com.varun.pgm.dto.response.RentLedgerResponse;
import com.varun.pgm.dto.response.RentLedgerSummaryResponse;
import com.varun.pgm.entity.Payment;
import com.varun.pgm.entity.RentLedger;
import com.varun.pgm.service.RentLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rent-ledger")
@Tag(name = "Rent Ledger", description = "Monthly Rent Ledger & Payment Management Endpoints")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class RentLedgerController {

    private final RentLedgerService rentLedgerService;

    @GetMapping
    @Operation(summary = "Get rent ledger for a billing month with pagination and status filter")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<Page<RentLedgerResponse>>> getLedgersByMonth(
            @RequestParam(defaultValue = "") String month,
            @RequestParam(required = false) RentLedger.LedgerStatus status,
            Pageable pageable) {

        String targetMonth = month.isEmpty() ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) : month;
        Page<RentLedgerResponse> response = rentLedgerService.getLedgersByMonth(targetMonth, status, pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", "Rent ledger retrieved successfully", response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get financial summary metrics for a billing month")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<RentLedgerSummaryResponse>> getSummaryByMonth(@RequestParam(defaultValue = "") String month) {
        String targetMonth = month.isEmpty() ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) : month;
        RentLedgerSummaryResponse response = rentLedgerService.getSummaryByMonth(targetMonth);
        return ResponseEntity.ok(new ApiResponse<>("success", "Rent ledger summary retrieved successfully", response));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get complete rent history and ledger for a tenant")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<List<RentLedgerResponse>>> getLedgersByTenant(@PathVariable Long tenantId) {
        List<RentLedgerResponse> response = rentLedgerService.getLedgersByTenantId(tenantId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant rent ledger retrieved successfully", response));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate monthly rent ledger entries for all active tenants")
    @RequirePermission("PAYMENT_CREATE")
    public ResponseEntity<ApiResponse<List<RentLedgerResponse>>> generateMonthlyLedger(@RequestParam(defaultValue = "") String month) {
        String targetMonth = month.isEmpty() ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) : month;
        List<RentLedgerResponse> response = rentLedgerService.generateMonthlyLedger(targetMonth);
        return ResponseEntity.ok(new ApiResponse<>("success", "Monthly rent ledger generated successfully", response));
    }

    @PostMapping("/{ledgerId}/pay")
    @Operation(summary = "Record full or partial payment against a rent ledger entry")
    @RequirePermission("PAYMENT_CREATE")
    public ResponseEntity<ApiResponse<PaymentResponse>> payRent(
            @PathVariable Long ledgerId,
            @Valid @RequestBody PayRentRequest request) {

        Payment payment = rentLedgerService.payRent(ledgerId, request);
        PaymentResponse response = new PaymentResponse(
                payment.getId(),
                payment.getTenant() != null ? payment.getTenant().getId() : null,
                payment.getTenant() != null ? payment.getTenant().getName() : null,
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMonth(),
                payment.getPaymentMethod(),
                payment.getReceiptNumber(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Rent payment recorded successfully", response));
    }
}
