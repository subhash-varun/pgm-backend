package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String roomNumber;
    private String issueTitle;
    private String description;
    private String imageUrl;
    private String status;
    private String priority;
    private String createdAt;
    private String resolvedAt;
    private String assignedTo;
}
