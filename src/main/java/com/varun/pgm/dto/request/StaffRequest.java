package com.varun.pgm.dto.request;

import com.varun.pgm.entity.Staff;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Admin ID is required")
    private Long adminId;

    private Staff.Role role = Staff.Role.STAFF;

    private Staff.Status status = Staff.Status.ACTIVE;
}
