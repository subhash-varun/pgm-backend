package com.varun.pgm.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionsRequest {

    @NotEmpty(message = "Permission IDs cannot be empty")
    private List<Long> permissionIds;
}
