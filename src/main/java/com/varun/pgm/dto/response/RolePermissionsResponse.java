package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionsResponse {

    private List<PermissionResponse> permissions;
}
