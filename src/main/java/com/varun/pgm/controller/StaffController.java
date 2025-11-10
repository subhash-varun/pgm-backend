package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.StaffRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.StaffResponse;
import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Staff;
import com.varun.pgm.service.StaffService;
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
@RequestMapping("/api/admin/staff")
@Tag(name = "Staff", description = "Staff management endpoints")
@SecurityRequirement(name = "bearer-key")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping
    @Operation(summary = "Create a new staff member")
    @RequirePermission("STAFF_CREATE")
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffRequest request) {
        Staff staff = new Staff();
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPassword(request.getPassword());

        // Set admin relationship
        if (request.getAdminId() != null) {
            Admin admin = new Admin();
            admin.setId(request.getAdminId());
            staff.setAdmin(admin);
        }

        staff.setRole(request.getRole());
        staff.setStatus(request.getStatus());

        Staff savedStaff = staffService.createStaff(staff);
        StaffResponse response = new StaffResponse(
                savedStaff.getId(),
                savedStaff.getName(),
                savedStaff.getEmail(),
                savedStaff.getAdmin() != null ? savedStaff.getAdmin().getId() : null,
                savedStaff.getRole(),
                savedStaff.getStatus(),
                savedStaff.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all staff members with pagination")
    @RequirePermission("STAFF_READ")
    public ResponseEntity<ApiResponse<Page<StaffResponse>>> getAllStaff(Pageable pageable) {
        Page<Staff> staff = staffService.getAllStaff(pageable);
        Page<StaffResponse> response = staff.map(s -> new StaffResponse(
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getAdmin() != null ? s.getAdmin().getId() : null,
                s.getRole(),
                s.getStatus(),
                s.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff by ID")
    @RequirePermission("STAFF_READ")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaffById(@PathVariable Long id) {
        Staff staff = staffService.getStaffById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        StaffResponse response = new StaffResponse(
                staff.getId(),
                staff.getName(),
                staff.getEmail(),
                staff.getAdmin() != null ? staff.getAdmin().getId() : null,
                staff.getRole(),
                staff.getStatus(),
                staff.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff retrieved successfully", response));
    }

    @GetMapping("/admin/{adminId}")
    @Operation(summary = "Get staff by admin ID")
    @RequirePermission("STAFF_READ")
    public ResponseEntity<ApiResponse<Page<StaffResponse>>> getStaffByAdminId(@PathVariable Long adminId, Pageable pageable) {
        List<Staff> staffList = staffService.getStaffByAdminId(adminId);
        // Convert List to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), staffList.size());
        List<Staff> subList = staffList.subList(start, end);
        Page<Staff> staff = new PageImpl<>(subList, pageable, staffList.size());

        Page<StaffResponse> response = staff.map(s -> new StaffResponse(
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getAdmin() != null ? s.getAdmin().getId() : null,
                s.getRole(),
                s.getStatus(),
                s.getCreatedAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update staff")
    @RequirePermission("STAFF_UPDATE")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffRequest request) {
        Staff staffDetails = new Staff();
        staffDetails.setName(request.getName());
        staffDetails.setEmail(request.getEmail());
        staffDetails.setPassword(request.getPassword());

        // Set admin relationship
        if (request.getAdminId() != null) {
            Admin admin = new Admin();
            admin.setId(request.getAdminId());
            staffDetails.setAdmin(admin);
        }

        staffDetails.setRole(request.getRole());
        staffDetails.setStatus(request.getStatus());

        Staff updatedStaff = staffService.updateStaff(id, staffDetails);
        StaffResponse response = new StaffResponse(
                updatedStaff.getId(),
                updatedStaff.getName(),
                updatedStaff.getEmail(),
                updatedStaff.getAdmin() != null ? updatedStaff.getAdmin().getId() : null,
                updatedStaff.getRole(),
                updatedStaff.getStatus(),
                updatedStaff.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete staff")
    @RequirePermission("STAFF_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Staff deleted successfully", null));
    }
}
