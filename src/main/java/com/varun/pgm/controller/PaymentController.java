package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.PaymentRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.PaymentResponse;
import com.varun.pgm.entity.Payment;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@Tag(name = "Payments", description = "Payment management endpoints")
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment")
    @RequirePermission("PAYMENT_CREATE")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = new Payment();
        // Set tenant relationship
        if (request.getTenantId() != null) {
            Tenant tenant = new Tenant();
            tenant.setId(request.getTenantId());
            payment.setTenant(tenant);
        }
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMonth(request.getPaymentMonth());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReceiptNumber(request.getReceiptNumber());
        payment.setStatus(request.getStatus());

        Payment savedPayment = paymentService.createPayment(payment);
        PaymentResponse response = new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getTenant() != null ? savedPayment.getTenant().getId() : null,
                savedPayment.getTenant() != null ? savedPayment.getTenant().getName() : null,
                savedPayment.getAmount(),
                savedPayment.getPaymentDate(),
                savedPayment.getPaymentMonth(),
                savedPayment.getPaymentMethod(),
                savedPayment.getReceiptNumber(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Payment created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all payments with pagination")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentService.getAllPayments(pageable);
        Page<PaymentResponse> response = payments.map(payment -> new PaymentResponse(
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
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Payments retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
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
        return ResponseEntity.ok(new ApiResponse<>("success", "Payment retrieved successfully", response));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get payments by tenant ID")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByTenantId(@PathVariable Long tenantId, Pageable pageable) {
        List<Payment> paymentList = paymentService.getPaymentsByTenantId(tenantId);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), paymentList.size());
        List<Payment> subList = paymentList.subList(start, end);
        Page<Payment> payments = new PageImpl<>(subList, pageable, paymentList.size());

        Page<PaymentResponse> response = payments.map(payment -> new PaymentResponse(
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
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Payments retrieved successfully", response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status, Pageable pageable) {
        List<Payment> paymentList = paymentService.getPaymentsByStatus(status);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), paymentList.size());
        List<Payment> subList = paymentList.subList(start, end);
        Page<Payment> payments = new PageImpl<>(subList, pageable, paymentList.size());

        Page<PaymentResponse> response = payments.map(payment -> new PaymentResponse(
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
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Payments retrieved successfully", response));
    }

    @GetMapping("/month/{paymentMonth}")
    @Operation(summary = "Get payments by month")
    @RequirePermission("PAYMENT_READ")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByMonth(@PathVariable String paymentMonth, Pageable pageable) {
        List<Payment> paymentList = paymentService.getPaymentsByMonth(paymentMonth);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), paymentList.size());
        List<Payment> subList = paymentList.subList(start, end);
        Page<Payment> payments = new PageImpl<>(subList, pageable, paymentList.size());

        Page<PaymentResponse> response = payments.map(payment -> new PaymentResponse(
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
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Payments retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment")
    @RequirePermission("PAYMENT_UPDATE")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        Payment paymentDetails = new Payment();
        // Set tenant relationship
        if (request.getTenantId() != null) {
            Tenant tenant = new Tenant();
            tenant.setId(request.getTenantId());
            paymentDetails.setTenant(tenant);
        }
        paymentDetails.setAmount(request.getAmount());
        paymentDetails.setPaymentDate(request.getPaymentDate());
        paymentDetails.setPaymentMonth(request.getPaymentMonth());
        paymentDetails.setPaymentMethod(request.getPaymentMethod());
        paymentDetails.setReceiptNumber(request.getReceiptNumber());
        paymentDetails.setStatus(request.getStatus());

        Payment updatedPayment = paymentService.updatePayment(id, paymentDetails);
        PaymentResponse response = new PaymentResponse(
                updatedPayment.getId(),
                updatedPayment.getTenant() != null ? updatedPayment.getTenant().getId() : null,
                updatedPayment.getTenant() != null ? updatedPayment.getTenant().getName() : null,
                updatedPayment.getAmount(),
                updatedPayment.getPaymentDate(),
                updatedPayment.getPaymentMonth(),
                updatedPayment.getPaymentMethod(),
                updatedPayment.getReceiptNumber(),
                updatedPayment.getStatus(),
                updatedPayment.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Payment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment")
    @RequirePermission("PAYMENT_DELETE")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Payment deleted successfully", null));
    }
}
