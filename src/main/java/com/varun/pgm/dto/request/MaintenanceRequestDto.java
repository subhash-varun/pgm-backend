package com.varun.pgm.dto.request;

import lombok.Data;

@Data
public class MaintenanceRequestDto {
    private Long tenantId;
    private Long roomId;
    private String issueTitle;
    private String description;
    private String imageUrl;
    private String priority;
    private String status;
    private String assignedTo;
}
