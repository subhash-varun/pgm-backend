package com.varun.pgm.service;

import com.varun.pgm.dto.TenantLifecycleDTOs.*;
import com.varun.pgm.entity.RentLedger;
import com.varun.pgm.entity.Room;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.entity.Tenant.TenantStatus;
import com.varun.pgm.entity.TenantSettlement;
import com.varun.pgm.repository.RentLedgerRepository;
import com.varun.pgm.repository.RoomRepository;
import com.varun.pgm.repository.TenantRepository;
import com.varun.pgm.repository.TenantSettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TenantLifecycleService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RentLedgerRepository rentLedgerRepository;

    @Autowired
    private TenantSettlementRepository tenantSettlementRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public void initiateNotice(Long tenantId, InitiateNoticeRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        if (tenant.getStatus() == TenantStatus.MOVED_OUT) {
            throw new RuntimeException("Tenant has already moved out.");
        }

        tenant.setNoticeDate(request.getNoticeDate() != null ? request.getNoticeDate() : LocalDate.now());
        tenant.setExpectedExitDate(request.getExpectedExitDate() != null ? request.getExpectedExitDate() : LocalDate.now().plusDays(30));
        tenant.setExitReason(request.getExitReason());
        tenant.setStatus(TenantStatus.NOTICE);

        tenantRepository.save(tenant);
    }

    @Transactional(readOnly = true)
    public SettlementSummaryResponse getSettlementSummary(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        List<RentLedger> ledgers = rentLedgerRepository.findByTenantIdOrderByBillingMonthDesc(tenantId);
        
        BigDecimal outstandingDues = ledgers.stream()
                .filter(l -> l.getStatus() != RentLedger.LedgerStatus.PAID)
                .map(RentLedger::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long unpaidCount = ledgers.stream()
                .filter(l -> l.getStatus() != RentLedger.LedgerStatus.PAID)
                .count();

        BigDecimal deposit = tenant.getDepositAmount() != null ? tenant.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal advance = tenant.getAdvanceBalance() != null ? tenant.getAdvanceBalance() : BigDecimal.ZERO;
        
        // Estimated Net Refund = Deposit + Advance - Outstanding Rent Dues
        BigDecimal netRefund = deposit.add(advance).subtract(outstandingDues);

        return SettlementSummaryResponse.builder()
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .roomNumber(tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : "N/A")
                .checkInDate(tenant.getCheckInDate())
                .noticeDate(tenant.getNoticeDate())
                .expectedExitDate(tenant.getExpectedExitDate())
                .securityDepositPaid(deposit)
                .advanceBalance(advance)
                .outstandingRentDues(outstandingDues)
                .unpaidLedgerCount(unpaidCount)
                .suggestedDamageCharges(BigDecimal.ZERO)
                .estimatedNetRefund(netRefund)
                .build();
    }

    @Transactional
    public SettlementResponse completeExit(Long tenantId, CompleteExitRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        if (tenant.getStatus() == TenantStatus.MOVED_OUT) {
            throw new RuntimeException("Tenant has already been checked out.");
        }

        List<RentLedger> ledgers = rentLedgerRepository.findByTenantIdOrderByBillingMonthDesc(tenantId);
        BigDecimal outstandingDues = ledgers.stream()
                .filter(l -> l.getStatus() != RentLedger.LedgerStatus.PAID)
                .map(RentLedger::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deposit = tenant.getDepositAmount() != null ? tenant.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal advance = tenant.getAdvanceBalance() != null ? tenant.getAdvanceBalance() : BigDecimal.ZERO;
        BigDecimal damage = request.getDamageCharges() != null ? request.getDamageCharges() : BigDecimal.ZERO;
        BigDecimal otherDeductions = request.getOtherDeductions() != null ? request.getOtherDeductions() : BigDecimal.ZERO;

        // Net Refund = (Deposit + Advance) - (Outstanding Dues + Damage + Other Deductions)
        BigDecimal netRefund = deposit.add(advance)
                .subtract(outstandingDues)
                .subtract(damage)
                .subtract(otherDeductions);

        LocalDate exitDate = request.getActualExitDate() != null ? request.getActualExitDate() : LocalDate.now();

        // 1. Create Settlement Record
        TenantSettlement settlement = new TenantSettlement();
        settlement.setTenant(tenant);
        settlement.setNoticeDate(tenant.getNoticeDate());
        settlement.setActualExitDate(exitDate);
        settlement.setSecurityDepositPaid(deposit);
        settlement.setOutstandingRentDues(outstandingDues);
        settlement.setDamageCharges(damage);
        settlement.setOtherDeductions(otherDeductions);
        settlement.setNetRefundAmount(netRefund);
        settlement.setPaymentMethod(request.getPaymentMethod());
        settlement.setRemarks(request.getRemarks());
        settlement.setStatus(TenantSettlement.SettlementStatus.SETTLED);
        settlement = tenantSettlementRepository.save(settlement);

        // 2. Update Tenant Status and release Room Occupancy
        tenant.setStatus(TenantStatus.MOVED_OUT);
        tenant.setActualExitDate(exitDate);
        tenant.setCheckOutDate(exitDate);
        
        Room room = tenant.getRoom();
        if (room != null) {
            long remainingActiveTenants = tenantRepository.findByRoomId(room.getId()).stream()
                    .filter(t -> !t.getId().equals(tenant.getId()) && t.getStatus() != TenantStatus.MOVED_OUT)
                    .count();
            if (remainingActiveTenants == 0) {
                room.setStatus(Room.RoomStatus.AVAILABLE);
                roomRepository.save(room);
            }
        }

        tenantRepository.save(tenant);

        return SettlementResponse.builder()
                .settlementId(settlement.getId())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .noticeDate(settlement.getNoticeDate())
                .actualExitDate(settlement.getActualExitDate())
                .securityDepositPaid(settlement.getSecurityDepositPaid())
                .outstandingRentDues(settlement.getOutstandingRentDues())
                .damageCharges(settlement.getDamageCharges())
                .otherDeductions(settlement.getOtherDeductions())
                .netRefundAmount(settlement.getNetRefundAmount())
                .paymentMethod(settlement.getPaymentMethod())
                .remarks(settlement.getRemarks())
                .status(settlement.getStatus().name())
                .settledAt(settlement.getSettledAt())
                .build();
    }
}
