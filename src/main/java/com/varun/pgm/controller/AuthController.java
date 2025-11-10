package com.varun.pgm.controller;

import com.varun.pgm.dto.request.LoginRequest;
import com.varun.pgm.dto.request.RegisterRequest;
import com.varun.pgm.dto.request.UpdateProfileRequest;
import com.varun.pgm.dto.response.AdminProfileResponse;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.LoginResponse;
import com.varun.pgm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new admin")
    public ResponseEntity<ApiResponse<AdminProfileResponse>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Admin registered successfully", null));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Login successful", response));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current admin profile")
    public ResponseEntity<ApiResponse<AdminProfileResponse>> getProfile() {
        AdminProfileResponse response = authService.getProfile();
        return ResponseEntity.ok(new ApiResponse<>("success", "Profile retrieved successfully", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current admin profile")
    public ResponseEntity<ApiResponse<AdminProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        AdminProfileResponse response = authService.updateProfile(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Profile updated successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current admin")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(new ApiResponse<>("success", "Logout successful", null));
    }
}