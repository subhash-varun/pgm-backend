package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String body;
    private String type;
    private Long senderId;
    private Long targetUserId;
    private String targetRole;
    private Long targetPropertyId;
    private Map<String, Object> payload;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Boolean delivered;
    private Integer deliveryAttempts;
}
