package com.varun.pgm.controller;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.dto.TenantLifecycleDTOs.*;
import com.varun.pgm.dto.response.ApiResponse;
import com.varun.pgm.service.TenantLifecycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tenants")
@Tag(name = "Tenant Lifecycle", description = "Tenant Notice Period & Exit Settlement Endpoints")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class TenantLifecycleController {

    private final TenantLifecycleService tenantLifecycleService;

    @PostMapping("/{tenantId}/notice")
    @Operation(summary = "Initiate move-out notice period for a tenant")
    @RequirePermission("TENANT_UPDATE")
    public ResponseEntity<ApiResponse<String>> initiateNotice(
            @PathVariable Long tenantId,
            @RequestBody InitiateNoticeRequest request) {
        tenantLifecycleService.initiateNotice(tenantId, request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Notice period initiated successfully", "OK"));
    }

    @GetMapping("/{tenantId}/settlement-summary")
    @Operation(summary = "Get exit settlement financial summary for a tenant")
    @RequirePermission("TENANT_READ")
    public ResponseEntity<ApiResponse<SettlementSummaryResponse>> getSettlementSummary(@PathVariable Long tenantId) {
        SettlementSummaryResponse summary = tenantLifecycleService.getSettlementSummary(tenantId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Settlement summary calculated successfully", summary));
    }

    @PostMapping("/{tenantId}/exit")
    @Operation(summary = "Finalize exit settlement and release tenant room")
    @RequirePermission("TENANT_UPDATE")
    public ResponseEntity<ApiResponse<SettlementResponse>> completeExit(
            @PathVariable Long tenantId,
            @RequestBody CompleteExitRequest request) {
        SettlementResponse response = tenantLifecycleService.completeExit(tenantId, request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Tenant exit completed and room released successfully", response));
    }
}
