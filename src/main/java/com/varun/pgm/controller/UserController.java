package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.response.AccessCheckResponse;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.UserRolesResponse;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User Roles", description = "User role assignment and access check endpoints")
public class UserController {

    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Assign role to user")
    @RequirePermission("ADMIN_UPDATE")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId, @RequestParam UserRole.UserType userType) {
        userRoleService.assignRoleToUser(userId, roleId, userType);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role assigned successfully", null));
    }

    @GetMapping("/{userId}/roles")
    @Operation(summary = "Get roles for user")
    @RequirePermission("ADMIN_READ")
    public ResponseEntity<ApiResponse<UserRolesResponse>> getUserRoles(@PathVariable Long userId, @RequestParam UserRole.UserType userType) {
        UserRolesResponse response = userRoleService.getUserRoles(userId, userType);
        return ResponseEntity.ok(new ApiResponse<>("success", "Roles retrieved successfully", response));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Remove role from user")
    @RequirePermission("ADMIN_UPDATE")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId, @RequestParam UserRole.UserType userType) {
        userRoleService.removeRoleFromUser(userId, roleId, userType);
        return ResponseEntity.ok(new ApiResponse<>("success", "Role removed successfully", null));
    }

    @GetMapping("/access/check")
    @Operation(summary = "Check if user has permission")
    @RequirePermission("ADMIN_READ")
    public ResponseEntity<ApiResponse<AccessCheckResponse>> checkPermission(@RequestParam String permission) {
        AccessCheckResponse response = userRoleService.checkPermission(permission);
        return ResponseEntity.ok(new ApiResponse<>("success", "Permission check completed", response));
    }
}
