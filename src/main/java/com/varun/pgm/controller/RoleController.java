package com.varun.pgm.controller;

import com.varun.pgm.dto.request.AssignPermissionsRequest;
import com.varun.pgm.dto.request.CreateRoleRequest;
import com.varun.pgm.dto.request.UpdateRoleRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.RolePermissionsResponse;
import com.varun.pgm.dto.response.RoleResponse;
import com.varun.pgm.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "Roles", description = "Role management endpoints")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all roles with pagination")
    public ResponseEntity<ApiResponse<Page<RoleResponse>>> getAllRoles(Pageable pageable) {
        Page<RoleResponse> response = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", "Roles retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role deleted successfully", null));
    }

    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "Assign permissions to role")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(@PathVariable Long roleId, @Valid @RequestBody AssignPermissionsRequest request) {
        roleService.assignPermissionsToRole(roleId, request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permissions assigned successfully", null));
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "Get permissions for role")
    public ResponseEntity<ApiResponse<RolePermissionsResponse>> getRolePermissions(@PathVariable Long roleId) {
        RolePermissionsResponse response = roleService.getRolePermissions(roleId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permissions retrieved successfully", response));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Remove permission from role")
    public ResponseEntity<ApiResponse<Void>> removePermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission removed successfully", null));
    }
}
