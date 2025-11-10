package com.varun.pgm.repository;

import com.varun.pgm.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    long countByStatus(Maintenance.MaintenanceStatus status);
}
