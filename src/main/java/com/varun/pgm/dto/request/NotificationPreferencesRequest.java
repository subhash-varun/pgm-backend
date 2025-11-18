package com.varun.pgm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesRequest {

    @NotNull(message = "Email enabled flag is required")
    private Boolean emailEnabled;

    @NotNull(message = "Push enabled flag is required")
    private Boolean pushEnabled;

    @NotNull(message = "SMS enabled flag is required")
    private Boolean smsEnabled;

    @NotNull(message = "Web enabled flag is required")
    private Boolean webEnabled;
}
