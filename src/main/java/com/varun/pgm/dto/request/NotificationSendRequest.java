package com.varun.pgm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Body must not exceed 2000 characters")
    private String body;

    @NotBlank(message = "Type is required")
    private String type;

    private Long targetUserId;

    private String targetRole;

    private Long targetPropertyId;

    private Map<String, Object> payload;

    private String priority = "NORMAL"; // NORMAL, HIGH, URGENT
}
