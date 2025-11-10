package com.varun.pgm.controller;

import com.varun.pgm.dto.request.CreatePermissionRequest;
import com.varun.pgm.dto.request.UpdatePermissionRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.PermissionResponse;
import com.varun.pgm.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "Permissions", description = "Permission management endpoints")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create a new permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all permissions with pagination")
    public ResponseEntity<ApiResponse<Page<PermissionResponse>>> getAllPermissions(Pageable pageable) {
        Page<PermissionResponse> response = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permissions retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(@PathVariable Long id, @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionResponse response = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission deleted successfully", null));
    }
}
