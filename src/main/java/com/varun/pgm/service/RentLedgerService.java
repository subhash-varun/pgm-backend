package com.varun.pgm.service;

import com.varun.pgm.dto.request.PayRentRequest;
import com.varun.pgm.dto.response.RentLedgerResponse;
import com.varun.pgm.dto.response.RentLedgerSummaryResponse;
import com.varun.pgm.entity.Payment;
import com.varun.pgm.entity.RentLedger;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.PaymentRepository;
import com.varun.pgm.repository.RentLedgerRepository;
import com.varun.pgm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentLedgerService {

    private final RentLedgerRepository rentLedgerRepository;
    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public List<RentLedgerResponse> generateMonthlyLedger(String billingMonth) {
        YearMonth ym = YearMonth.parse(billingMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate dueDate = ym.atDay(5); // Default due date 5th of the month
        LocalDate now = LocalDate.now();

        List<Tenant> activeTenants = tenantRepository.findAll().stream()
                .filter(t -> Tenant.TenantStatus.ACTIVE.equals(t.getStatus()))
                .toList();

        for (Tenant tenant : activeTenants) {
            rentLedgerRepository.findByTenantIdAndBillingMonth(tenant.getId(), billingMonth)
                    .orElseGet(() -> {
                        BigDecimal rent = tenant.getRoom() != null ? tenant.getRoom().getRentAmount() : BigDecimal.ZERO;
                        RentLedger ledger = new RentLedger();
                        ledger.setTenant(tenant);
                        ledger.setBillingMonth(billingMonth);
                        ledger.setBaseRent(rent);
                        ledger.setUtilityCharges(BigDecimal.ZERO);
                        ledger.setLateFee(BigDecimal.ZERO);
                        ledger.setTotalAmount(rent);
                        ledger.setPaidAmount(BigDecimal.ZERO);
                        ledger.setBalanceDue(rent);
                        ledger.setDueDate(dueDate);
                        
                        if (now.isAfter(dueDate) && rent.compareTo(BigDecimal.ZERO) > 0) {
                            ledger.setStatus(RentLedger.LedgerStatus.OVERDUE);
                        } else {
                            ledger.setStatus(RentLedger.LedgerStatus.UNPAID);
                        }
                        ledger.setCreatedAt(LocalDateTime.now());
                        return rentLedgerRepository.save(ledger);
                    });
        }

        return getLedgersForMonthList(billingMonth);
    }

    @Transactional
    public Payment payRent(Long ledgerId, PayRentRequest request) {
        RentLedger ledger = rentLedgerRepository.findById(ledgerId)
                .orElseThrow(() -> new RuntimeException("Rent ledger entry not found"));

        Tenant tenant = ledger.getTenant();
        BigDecimal paymentAmount = request.getAmount();

        if (request.isApplyAdvance() && tenant.getAdvanceBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal advanceToUse = tenant.getAdvanceBalance().min(ledger.getBalanceDue());
            paymentAmount = paymentAmount.add(advanceToUse);
            tenant.setAdvanceBalance(tenant.getAdvanceBalance().subtract(advanceToUse));
        }

        BigDecimal newPaidAmount = ledger.getPaidAmount().add(paymentAmount);
        ledger.setPaidAmount(newPaidAmount);

        BigDecimal newBalanceDue = ledger.getTotalAmount().subtract(newPaidAmount);

        if (newBalanceDue.compareTo(BigDecimal.ZERO) <= 0) {
            if (newBalanceDue.compareTo(BigDecimal.ZERO) < 0) {
                // Excess payment converted to advance balance
                BigDecimal excess = newBalanceDue.abs();
                tenant.setAdvanceBalance(tenant.getAdvanceBalance().add(excess));
                newBalanceDue = BigDecimal.ZERO;
            }
            ledger.setStatus(RentLedger.LedgerStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            ledger.setStatus(RentLedger.LedgerStatus.PARTIAL);
        }

        ledger.setBalanceDue(newBalanceDue);
        rentLedgerRepository.save(ledger);
        tenantRepository.save(tenant);

        // Record payment transaction
        Payment payment = new Payment();
        payment.setTenant(tenant);
        payment.setLedger(ledger);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMonth(ledger.getBillingMonth());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReceiptNumber("RCP-" + System.currentTimeMillis());
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Page<RentLedgerResponse> getLedgersByMonth(String month, RentLedger.LedgerStatus status, Pageable pageable) {
        Page<RentLedger> ledgers;
        if (status != null) {
            ledgers = rentLedgerRepository.findByBillingMonthAndStatus(month, status, pageable);
        } else {
            ledgers = rentLedgerRepository.findByBillingMonth(month, pageable);
        }
        return ledgers.map(this::mapToResponse);
    }

    public List<RentLedgerResponse> getLedgersByTenantId(Long tenantId) {
        return rentLedgerRepository.findByTenantIdOrderByBillingMonthDesc(tenantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RentLedgerSummaryResponse getSummaryByMonth(String month) {
        BigDecimal totalBilled = rentLedgerRepository.sumTotalBilledByMonth(month);
        BigDecimal totalCollected = rentLedgerRepository.sumTotalCollectedByMonth(month);
        BigDecimal totalPending = rentLedgerRepository.sumTotalPendingByMonth(month);
        long overdueCount = rentLedgerRepository.countOverdueByMonth(month);

        return new RentLedgerSummaryResponse(
                month,
                totalBilled != null ? totalBilled : BigDecimal.ZERO,
                totalCollected != null ? totalCollected : BigDecimal.ZERO,
                totalPending != null ? totalPending : BigDecimal.ZERO,
                overdueCount,
                tenantRepository.countByStatus(Tenant.TenantStatus.ACTIVE)
        );
    }

    private List<RentLedgerResponse> getLedgersForMonthList(String month) {
        return rentLedgerRepository.findByBillingMonth(month, Pageable.unpaged())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RentLedgerResponse mapToResponse(RentLedger ledger) {
        return new RentLedgerResponse(
                ledger.getId(),
                ledger.getTenant().getId(),
                ledger.getTenant().getName(),
                ledger.getTenant().getRoom() != null ? ledger.getTenant().getRoom().getRoomNumber() : "N/A",
                ledger.getBillingMonth(),
                ledger.getBaseRent(),
                ledger.getUtilityCharges(),
                ledger.getLateFee(),
                ledger.getTotalAmount(),
                ledger.getPaidAmount(),
                ledger.getBalanceDue(),
                ledger.getTenant().getAdvanceBalance(),
                ledger.getDueDate(),
                ledger.getStatus(),
                ledger.getCreatedAt()
        );
    }
}
