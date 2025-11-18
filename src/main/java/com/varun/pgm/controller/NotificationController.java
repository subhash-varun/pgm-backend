package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.request.NotificationSendRequest;
import com.varun.pgm.dto.request.NotificationPreferencesRequest;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.dto.response.NotificationPageResponse;
import com.varun.pgm.dto.response.NotificationResponse;
import com.varun.pgm.dto.response.NotificationPreferencesResponse;
import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Staff;
import com.varun.pgm.repository.AdminRepository;
import com.varun.pgm.repository.StaffRepository;
import com.varun.pgm.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;

    @PostMapping("/send")
    // @RequirePermission("NOTIFICATION_CREATE") // Temporarily disabled for testing
    @Operation(summary = "Send a notification", description = "Send a notification to specified targets")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationSendRequest request) {

        Long senderId = getCurrentUserId();
        NotificationResponse response = notificationService.sendNotification(request, senderId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Notification sent successfully", response));
    }

    @GetMapping
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Get user notifications", description = "Get paginated notifications for the current user")
    public ResponseEntity<ApiResponse<NotificationPageResponse>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean read) {

        Long userId = getCurrentUserId();
        NotificationPageResponse response = notificationService.getUserNotifications(userId, page, size, read);

        return ResponseEntity.ok(new ApiResponse<>("success", "Notifications retrieved successfully", response));
    }

    @GetMapping("/unread-count")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Get unread count", description = "Get the count of unread notifications for the current user")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long userId = getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Unread count retrieved successfully", count));
    }

    @PatchMapping("/{id}/read")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Notification marked as read", null));
    }

    @PatchMapping("/mark-all-read")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the current user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(new ApiResponse<>("success", "All notifications marked as read", null));
    }

    @GetMapping("/{id}")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Get notification details", description = "Get detailed information about a specific notification")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(@PathVariable Long id) {
        // TODO: Implement get single notification with proper authorization
        // For now, return a placeholder response
        return ResponseEntity.ok(new ApiResponse<>("success", "Feature not implemented yet", null));
    }

    @GetMapping("/preferences")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Get notification preferences", description = "Get notification preferences for the current user")
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>> getPreferences() {
        Long userId = getCurrentUserId();
        NotificationPreferencesResponse response = notificationService.getUserPreferences(userId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Preferences retrieved successfully", response));
    }

    @PutMapping("/preferences")
    @RequirePermission("NOTIFICATION_READ")
    @Operation(summary = "Update notification preferences", description = "Update notification preferences for the current user")
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>> updatePreferences(
            @Valid @RequestBody NotificationPreferencesRequest request) {

        Long userId = getCurrentUserId();
        NotificationPreferencesResponse response = notificationService.updateUserPreferences(userId, request);

        return ResponseEntity.ok(new ApiResponse<>("success", "Preferences updated successfully", response));
    }

    @GetMapping("/debug-permissions")
    @Operation(summary = "Debug user permissions", description = "Check current user's permissions")
    public ResponseEntity<ApiResponse<List<String>>> debugPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> permissions = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>("success", "User permissions", permissions));
    }

    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            // Look up user ID by email
            // Try Admin first
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isPresent()) {
                return adminOpt.get().getId();
            }

            // Try Staff
            Optional<Staff> staffOpt = staffRepository.findByEmail(email);
            if (staffOpt.isPresent()) {
                return staffOpt.get().getId();
            }

            throw new RuntimeException("User not found with email: " + email);
        }
        throw new RuntimeException("Unable to get current user ID");
    }
}
