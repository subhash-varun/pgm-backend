package com.varun.pgm.repository;

import com.varun.pgm.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenantId(Long tenantId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    BigDecimal sumPaidPaymentsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    long countPendingPayments();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PENDING'")
    BigDecimal sumPendingPayments();

    @Query("SELECT COALESCE(SUM(t.depositAmount), 0) FROM Tenant t WHERE t.status = 'ACTIVE'")
    BigDecimal sumActiveTenantDeposits();

    @Query("SELECT p FROM Payment p WHERE p.status = 'PAID' ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPayments();

    @Query("SELECT COALESCE(SUM(r.rentAmount), 0) FROM Room r WHERE r.status = 'OCCUPIED'")
    BigDecimal calculateExpectedMonthlyRevenue();

    @Query("SELECT MONTH(p.paymentDate) as month, YEAR(p.paymentDate) as year, SUM(p.amount) as amount " +
           "FROM Payment p " +
           "WHERE p.status = 'PAID' AND p.paymentDate >= :sixMonthsAgo " +
           "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate) " +
           "ORDER BY YEAR(p.paymentDate) DESC, MONTH(p.paymentDate) DESC")
    List<Object[]> findMonthlyRevenueLastSixMonths(@Param("sixMonthsAgo") LocalDate sixMonthsAgo);
}
