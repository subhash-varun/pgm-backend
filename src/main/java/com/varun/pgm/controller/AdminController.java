package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.AdminRequest;
import com.varun.pgm.dto.response.AdminResponse;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.DashboardSummaryResponse;
import com.varun.pgm.entity.Admin;
import com.varun.pgm.service.AdminService;
import com.varun.pgm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management and dashboard endpoints")
@SecurityRequirement(name = "bearer-key")
public class AdminController {

    private final AdminService adminService;

    private final DashboardService dashboardService;

    public AdminController(AdminService adminService, DashboardService dashboardService) {
        this.adminService = adminService;
        this.dashboardService = dashboardService;
    }

    @PostMapping
    @Operation(summary = "Create a new admin")
    @RequirePermission("ADMIN_CREATE")
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(@Valid @RequestBody AdminRequest request) {
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword());
        admin.setContactNo(request.getContactNo());

        Admin savedAdmin = adminService.createAdmin(admin);
        AdminResponse response = new AdminResponse(
                savedAdmin.getId(),
                savedAdmin.getName(),
                savedAdmin.getEmail(),
                savedAdmin.getContactNo(),
                savedAdmin.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Admin created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all admins with pagination")
    @RequirePermission("ADMIN_READ")
    public ResponseEntity<ApiResponse<Page<AdminResponse>>> getAllAdmins(Pageable pageable) {
        Page<Admin> admins = adminService.getAllAdmins(pageable);
        Page<AdminResponse> response = admins.map(admin -> new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getContactNo(),
                admin.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Admins retrieved successfully", response));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get admin by ID")
    @RequirePermission("ADMIN_READ")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        AdminResponse response = new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getContactNo(),
                admin.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Admin retrieved successfully", response));
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update admin")
    @RequirePermission("ADMIN_UPDATE")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequest request) {
        Admin adminDetails = new Admin();
        adminDetails.setName(request.getName());
        adminDetails.setEmail(request.getEmail());
        adminDetails.setPassword(request.getPassword());
        adminDetails.setContactNo(request.getContactNo());

        Admin updatedAdmin = adminService.updateAdmin(id, adminDetails);
        AdminResponse response = new AdminResponse(
                updatedAdmin.getId(),
                updatedAdmin.getName(),
                updatedAdmin.getEmail(),
                updatedAdmin.getContactNo(),
                updatedAdmin.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Admin updated successfully", response));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete admin")
    @RequirePermission("ADMIN_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Admin deleted successfully", null));
    }

    @GetMapping("/dashboard/summary")
    @Operation(summary = "Get admin dashboard summary")
    @RequirePermission("DASHBOARD_VIEW")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(new ApiResponse<>("success", "Dashboard summary retrieved successfully", summary));
    }
}
