package com.varun.pgm.service;

import com.varun.pgm.entity.Payment;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.PaymentRepository;
import com.varun.pgm.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public Payment createPayment(Payment payment) {
        // Validate tenant exists
        if (payment.getTenant() != null && payment.getTenant().getId() != null) {
            Tenant tenant = tenantRepository.findById(payment.getTenant().getId())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));
            payment.setTenant(tenant);
        }

        // Generate receipt number if not provided
        if (payment.getReceiptNumber() == null || payment.getReceiptNumber().isEmpty()) {
            payment.setReceiptNumber("RCP-" + System.currentTimeMillis());
        }

        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByTenantId(Long tenantId) {
        return paymentRepository.findByTenantId(tenantId);
    }

    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus().equals(status))
                .toList();
    }

    public List<Payment> getPaymentsByMonth(String paymentMonth) {
        return paymentRepository.findAll().stream()
                .filter(payment -> paymentMonth.equals(payment.getPaymentMonth()))
                .toList();
    }

    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Update tenant if provided
        if (paymentDetails.getTenant() != null && paymentDetails.getTenant().getId() != null) {
            Tenant tenant = tenantRepository.findById(paymentDetails.getTenant().getId())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));
            payment.setTenant(tenant);
        }

        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        payment.setPaymentMonth(paymentDetails.getPaymentMonth());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setReceiptNumber(paymentDetails.getReceiptNumber());
        payment.setStatus(paymentDetails.getStatus());

        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
    }
}
