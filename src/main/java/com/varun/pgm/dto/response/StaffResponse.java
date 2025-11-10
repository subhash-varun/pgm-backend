package com.varun.pgm.dto.response;

import com.varun.pgm.entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {

    private Long id;
    private String name;
    private String email;
    private Long adminId;
    private Staff.Role role;
    private Staff.Status status;
    private LocalDateTime createdAt;
}
