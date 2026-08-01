package com.varun.pgm.repository;

import com.varun.pgm.entity.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    long countByStatus(Maintenance.MaintenanceStatus status);

    @Query("SELECT m FROM Maintenance m LEFT JOIN m.reportedBy t LEFT JOIN m.room r " +
           "WHERE (:status IS NULL OR m.status = :status) " +
           "AND (:priority IS NULL OR m.priority = :priority)")
    Page<Maintenance> findAllByStatusAndPriority(
            @Param("status") Maintenance.MaintenanceStatus status,
            @Param("priority") Maintenance.MaintenancePriority priority,
            Pageable pageable
    );

    @Query("SELECT m FROM Maintenance m LEFT JOIN m.reportedBy t LEFT JOIN m.room r " +
           "WHERE (:status IS NULL OR m.status = :status) " +
           "AND (:priority IS NULL OR m.priority = :priority) " +
           "AND (LOWER(m.issue) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Maintenance> searchByStatusPriorityAndTerm(
            @Param("status") Maintenance.MaintenanceStatus status,
            @Param("priority") Maintenance.MaintenancePriority priority,
            @Param("search") String search,
            Pageable pageable
    );
}
