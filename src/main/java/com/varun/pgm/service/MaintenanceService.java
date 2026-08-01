package com.varun.pgm.service;

import com.varun.pgm.dto.request.MaintenanceRequestDto;
import com.varun.pgm.dto.response.MaintenanceResponse;
import com.varun.pgm.entity.Maintenance;
import com.varun.pgm.entity.Room;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.MaintenanceRepository;
import com.varun.pgm.repository.RoomRepository;
import com.varun.pgm.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              TenantRepository tenantRepository,
                              RoomRepository roomRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
    }

    public Page<MaintenanceResponse> getMaintenanceRequests(String statusStr, String priorityStr, String search, Pageable pageable) {
        Maintenance.MaintenanceStatus status = null;
        if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                if ("OPEN".equalsIgnoreCase(statusStr)) {
                    status = Maintenance.MaintenanceStatus.PENDING;
                } else {
                    status = Maintenance.MaintenanceStatus.valueOf(statusStr.toUpperCase());
                }
            } catch (Exception ignored) {}
        }

        Maintenance.MaintenancePriority priority = null;
        if (priorityStr != null && !priorityStr.isBlank() && !"ALL".equalsIgnoreCase(priorityStr)) {
            try {
                priority = Maintenance.MaintenancePriority.valueOf(priorityStr.toUpperCase());
            } catch (Exception ignored) {}
        }

        String searchPattern = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<Maintenance> page = maintenanceRepository.searchMaintenanceRequests(status, priority, searchPattern, pageable);
        return page.map(this::mapToResponse);
    }

    public MaintenanceResponse getById(Long id) {
        Maintenance m = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found: " + id));
        return mapToResponse(m);
    }

    public MaintenanceResponse createRequest(MaintenanceRequestDto dto) {
        Maintenance m = new Maintenance();
        if (dto.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(dto.getTenantId()).orElse(null);
            m.setReportedBy(tenant);
        }
        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId()).orElse(null);
            m.setRoom(room);
        }
        m.setIssue(dto.getIssueTitle() != null ? dto.getIssueTitle() : "General Maintenance");
        m.setDescription(dto.getDescription());
        m.setImageUrl(dto.getImageUrl());
        m.setStatus(Maintenance.MaintenanceStatus.PENDING);
        if (dto.getPriority() != null) {
            try {
                m.setPriority(Maintenance.MaintenancePriority.valueOf(dto.getPriority().toUpperCase()));
            } catch (Exception e) {
                m.setPriority(Maintenance.MaintenancePriority.MEDIUM);
            }
        }
        m.setCreatedAt(LocalDateTime.now());
        Maintenance saved = maintenanceRepository.save(m);
        return mapToResponse(saved);
    }

    public MaintenanceResponse updateStatus(Long id, String statusStr) {
        Maintenance m = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found: " + id));

        try {
            Maintenance.MaintenanceStatus newStatus;
            if ("OPEN".equalsIgnoreCase(statusStr)) {
                newStatus = Maintenance.MaintenanceStatus.PENDING;
            } else {
                newStatus = Maintenance.MaintenanceStatus.valueOf(statusStr.toUpperCase());
            }
            m.setStatus(newStatus);
            if (newStatus == Maintenance.MaintenanceStatus.RESOLVED) {
                m.setResolvedAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid maintenance status: " + statusStr);
        }

        Maintenance saved = maintenanceRepository.save(m);
        return mapToResponse(saved);
    }

    public MaintenanceResponse assignStaff(Long id, String assignedTo) {
        Maintenance m = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found: " + id));

        m.setAssignedTo(assignedTo);
        Maintenance saved = maintenanceRepository.save(m);
        return mapToResponse(saved);
    }

    public void delete(Long id) {
        maintenanceRepository.deleteById(id);
    }

    private MaintenanceResponse mapToResponse(Maintenance m) {
        String statusMapped = m.getStatus() != null ? m.getStatus().name() : "OPEN";
        if ("PENDING".equals(statusMapped)) {
            statusMapped = "OPEN";
        }

        return MaintenanceResponse.builder()
                .id(m.getId())
                .tenantId(m.getReportedBy() != null ? m.getReportedBy().getId() : null)
                .tenantName(m.getReportedBy() != null ? m.getReportedBy().getName() : "System / N/A")
                .roomNumber(m.getRoom() != null ? m.getRoom().getRoomNumber() : (m.getReportedBy() != null && m.getReportedBy().getRoom() != null ? m.getReportedBy().getRoom().getRoomNumber() : "N/A"))
                .issueTitle(m.getIssue())
                .description(m.getDescription())
                .imageUrl(m.getImageUrl())
                .status(statusMapped)
                .priority(m.getPriority() != null ? m.getPriority().name() : "MEDIUM")
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : LocalDateTime.now().toString())
                .resolvedAt(m.getResolvedAt() != null ? m.getResolvedAt().toString() : null)
                .assignedTo(m.getAssignedTo())
                .build();
    }
}
