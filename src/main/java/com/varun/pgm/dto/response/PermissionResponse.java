package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {

    private Long id;
    private String key;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
