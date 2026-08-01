package com.varun.pgm.repository;

import com.varun.pgm.entity.RentLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentLedgerRepository extends JpaRepository<RentLedger, Long> {

    Optional<RentLedger> findByTenantIdAndBillingMonth(Long tenantId, String billingMonth);

    List<RentLedger> findByTenantIdOrderByBillingMonthDesc(Long tenantId);

    Page<RentLedger> findByBillingMonth(String billingMonth, Pageable pageable);

    Page<RentLedger> findByBillingMonthAndStatus(String billingMonth, RentLedger.LedgerStatus status, Pageable pageable);

    @Query("SELECT SUM(r.totalAmount) FROM RentLedger r WHERE r.billingMonth = :month")
    BigDecimal sumTotalBilledByMonth(@Param("month") String month);

    @Query("SELECT SUM(r.paidAmount) FROM RentLedger r WHERE r.billingMonth = :month")
    BigDecimal sumTotalCollectedByMonth(@Param("month") String month);

    @Query("SELECT SUM(r.balanceDue) FROM RentLedger r WHERE r.billingMonth = :month")
    BigDecimal sumTotalPendingByMonth(@Param("month") String month);

    @Query("SELECT COUNT(r) FROM RentLedger r WHERE r.billingMonth = :month AND r.status = 'OVERDUE'")
    long countOverdueByMonth(@Param("month") String month);
}
