package com.varun.pgm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {

    @NotBlank(message = "Permission key is required")
    @Size(min = 2, max = 100, message = "Permission key must be between 2 and 100 characters")
    private String key;

    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}
