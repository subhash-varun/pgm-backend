package com.varun.pgm.repository;

import com.varun.pgm.entity.TenantSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantSettlementRepository extends JpaRepository<TenantSettlement, Long> {
    Optional<TenantSettlement> findByTenantId(Long tenantId);
}
