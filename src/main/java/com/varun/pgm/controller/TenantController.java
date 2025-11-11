package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.TenantRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.TenantResponse;
import com.varun.pgm.entity.Room;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.RoomRepository;
import com.varun.pgm.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tenants")
@Tag(name = "Tenants", description = "Tenant management endpoints")
@SecurityRequirement(name = "bearer-key")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    @RequirePermission("TENANT_CREATE")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@Valid @RequestBody TenantRequest request) {
        Tenant tenant = new Tenant();
        // Set room relationship
        if (request.getRoomNumber() != null) {
            Room room = roomRepository.findByRoomNumber(request.getRoomNumber())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            tenant.setRoom(room);
        }
        tenant.setName(request.getName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setIdProofType(request.getIdProofType());
        tenant.setIdProofNumber(request.getIdProofNumber());
        tenant.setCheckInDate(request.getCheckInDate());
        tenant.setCheckOutDate(request.getCheckOutDate());
        tenant.setDepositAmount(request.getDepositAmount());
        tenant.setStatus(request.getStatus());

        Tenant savedTenant = tenantService.createTenant(tenant);
        TenantResponse response = new TenantResponse(
                savedTenant.getId(),
                savedTenant.getRoom() != null ? savedTenant.getRoom().getId() : null,
                savedTenant.getRoom() != null ? savedTenant.getRoom().getRoomNumber() : null,
                savedTenant.getName(),
                savedTenant.getEmail(),
                savedTenant.getPhone(),
                savedTenant.getIdProofType(),
                savedTenant.getIdProofNumber(),
                savedTenant.getCheckInDate(),
                savedTenant.getCheckOutDate(),
                savedTenant.getDepositAmount(),
                savedTenant.getStatus(),
                savedTenant.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all tenants with pagination")
    @RequirePermission("TENANT_READ")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getAllTenants(Pageable pageable) {
        Page<Tenant> tenants = tenantService.getAllTenants(pageable);
        Page<TenantResponse> response = tenants.map(tenant -> new TenantResponse(
                tenant.getId(),
                tenant.getRoom() != null ? tenant.getRoom().getId() : null,
                tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : null,
                tenant.getName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getIdProofType(),
                tenant.getIdProofNumber(),
                tenant.getCheckInDate(),
                tenant.getCheckOutDate(),
                tenant.getDepositAmount(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenants retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    @RequirePermission("TENANT_READ")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(@PathVariable Long id) {
        Tenant tenant = tenantService.getTenantById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        TenantResponse response = new TenantResponse(
                tenant.getId(),
                tenant.getRoom() != null ? tenant.getRoom().getId() : null,
                tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : null,
                tenant.getName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getIdProofType(),
                tenant.getIdProofNumber(),
                tenant.getCheckInDate(),
                tenant.getCheckOutDate(),
                tenant.getDepositAmount(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant retrieved successfully", response));
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get tenants by room ID")
    @RequirePermission("TENANT_READ")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getTenantsByRoomId(@PathVariable Long roomId, Pageable pageable) {
        List<Tenant> tenantList = tenantService.getTenantsByRoomId(roomId);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), tenantList.size());
        List<Tenant> subList = tenantList.subList(start, end);
        Page<Tenant> tenants = new PageImpl<>(subList, pageable, tenantList.size());

        Page<TenantResponse> response = tenants.map(tenant -> new TenantResponse(
                tenant.getId(),
                tenant.getRoom() != null ? tenant.getRoom().getId() : null,
                tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : null,
                tenant.getName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getIdProofType(),
                tenant.getIdProofNumber(),
                tenant.getCheckInDate(),
                tenant.getCheckOutDate(),
                tenant.getDepositAmount(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenants retrieved successfully", response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tenants by status")
    @RequirePermission("TENANT_READ")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getTenantsByStatus(@PathVariable Tenant.TenantStatus status, Pageable pageable) {
        List<Tenant> tenantList = tenantService.getTenantsByStatus(status);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), tenantList.size());
        List<Tenant> subList = tenantList.subList(start, end);
        Page<Tenant> tenants = new PageImpl<>(subList, pageable, tenantList.size());

        Page<TenantResponse> response = tenants.map(tenant -> new TenantResponse(
                tenant.getId(),
                tenant.getRoom() != null ? tenant.getRoom().getId() : null,
                tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : null,
                tenant.getName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getIdProofType(),
                tenant.getIdProofNumber(),
                tenant.getCheckInDate(),
                tenant.getCheckOutDate(),
                tenant.getDepositAmount(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenants retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant")
    @RequirePermission("TENANT_UPDATE")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantRequest request) {
        Tenant tenantDetails = new Tenant();
        // Set room relationship
        if (request.getRoomNumber() != null) {
            Room room = roomRepository.findByRoomNumber(request.getRoomNumber())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            tenantDetails.setRoom(room);
        }
        tenantDetails.setName(request.getName());
        tenantDetails.setEmail(request.getEmail());
        tenantDetails.setPhone(request.getPhone());
        tenantDetails.setIdProofType(request.getIdProofType());
        tenantDetails.setIdProofNumber(request.getIdProofNumber());
        tenantDetails.setCheckInDate(request.getCheckInDate());
        tenantDetails.setCheckOutDate(request.getCheckOutDate());
        tenantDetails.setDepositAmount(request.getDepositAmount());
        tenantDetails.setStatus(request.getStatus());

        Tenant updatedTenant = tenantService.updateTenant(id, tenantDetails);
        TenantResponse response = new TenantResponse(
                updatedTenant.getId(),
                updatedTenant.getRoom() != null ? updatedTenant.getRoom().getId() : null,
                updatedTenant.getRoom() != null ? updatedTenant.getRoom().getRoomNumber() : null,
                updatedTenant.getName(),
                updatedTenant.getEmail(),
                updatedTenant.getPhone(),
                updatedTenant.getIdProofType(),
                updatedTenant.getIdProofNumber(),
                updatedTenant.getCheckInDate(),
                updatedTenant.getCheckOutDate(),
                updatedTenant.getDepositAmount(),
                updatedTenant.getStatus(),
                updatedTenant.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant")
    @RequirePermission("TENANT_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant deleted successfully", null));
    }
}
