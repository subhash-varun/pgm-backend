package com.varun.pgm.repository;

import com.varun.pgm.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByRoomId(Long roomId);

    long countByStatus(Tenant.TenantStatus status);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.checkInDate >= :startOfMonth AND t.checkInDate <= :endOfMonth")
    long countNewTenantsInMonth(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.checkOutDate >= :startOfMonth AND t.checkOutDate <= :endOfMonth")
    long countCheckoutsInMonth(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT t FROM Tenant t WHERE t.status = 'ACTIVE' ORDER BY t.checkInDate DESC")
    List<Tenant> findRecentActiveTenants();
}
