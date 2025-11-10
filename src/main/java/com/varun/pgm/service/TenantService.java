package com.varun.pgm.service;

import com.varun.pgm.entity.Room;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.RoomRepository;
import com.varun.pgm.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Tenant createTenant(Tenant tenant) {
        // Validate room exists
        if (tenant.getRoom() != null && tenant.getRoom().getId() != null) {
            Room room = roomRepository.findById(tenant.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            tenant.setRoom(room);
        }

        tenant.setCreatedAt(LocalDateTime.now());
        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Page<Tenant> getAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable);
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public List<Tenant> getTenantsByRoomId(Long roomId) {
        return tenantRepository.findByRoomId(roomId);
    }

    public List<Tenant> getTenantsByStatus(Tenant.TenantStatus status) {
        return tenantRepository.findAll().stream()
                .filter(tenant -> tenant.getStatus().equals(status))
                .toList();
    }

    public Tenant updateTenant(Long id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // Update room if provided
        if (tenantDetails.getRoom() != null && tenantDetails.getRoom().getId() != null) {
            Room room = roomRepository.findById(tenantDetails.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            tenant.setRoom(room);
        }

        tenant.setName(tenantDetails.getName());
        tenant.setEmail(tenantDetails.getEmail());
        tenant.setPhone(tenantDetails.getPhone());
        tenant.setIdProofType(tenantDetails.getIdProofType());
        tenant.setIdProofNumber(tenantDetails.getIdProofNumber());
        tenant.setCheckInDate(tenantDetails.getCheckInDate());
        tenant.setCheckOutDate(tenantDetails.getCheckOutDate());
        tenant.setDepositAmount(tenantDetails.getDepositAmount());
        tenant.setStatus(tenantDetails.getStatus());

        return tenantRepository.save(tenant);
    }

    public void deleteTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenantRepository.delete(tenant);
    }
}
