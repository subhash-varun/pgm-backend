package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.MaintenanceRequestDto;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.MaintenanceResponse;
import com.varun.pgm.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/maintenance")
@Tag(name = "Maintenance", description = "Maintenance request management endpoints")
@SecurityRequirement(name = "bearer-key")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    @Operation(summary = "Get all maintenance requests with search & filters")
    @RequirePermission("MAINTENANCE_READ")
    public ResponseEntity<ApiResponse<Page<MaintenanceResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<MaintenanceResponse> page = maintenanceService.getMaintenanceRequests(status, priority, search, pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", "Maintenance requests retrieved successfully", page));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get maintenance request by ID")
    @RequirePermission("MAINTENANCE_READ")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> getById(@PathVariable Long id) {
        MaintenanceResponse res = maintenanceService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Maintenance request retrieved successfully", res));
    }

    @PostMapping
    @Operation(summary = "Create maintenance request")
    @RequirePermission("MAINTENANCE_CREATE")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> create(@RequestBody MaintenanceRequestDto dto) {
        MaintenanceResponse res = maintenanceService.createRequest(dto);
        return ResponseEntity.ok(new ApiResponse<>("success", "Maintenance request created successfully", res));
    }

    @PatchMapping("/{id:\\d+}/status")
    @Operation(summary = "Update maintenance request status")
    @RequirePermission("MAINTENANCE_UPDATE")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        MaintenanceResponse res = maintenanceService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>("success", "Maintenance status updated", res));
    }

    @PatchMapping("/{id:\\d+}/assign")
    @Operation(summary = "Assign staff to maintenance request")
    @RequirePermission("MAINTENANCE_UPDATE")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> assignStaff(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String assignedTo = body.get("assignedTo");
        MaintenanceResponse res = maintenanceService.assignStaff(id, assignedTo);
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff assigned to maintenance request", res));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete maintenance request")
    @RequirePermission("MAINTENANCE_DELETE")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Maintenance request deleted successfully", null));
    }
}
