package com.varun.pgm.service;

import com.varun.pgm.dto.request.AssignPermissionsRequest;
import com.varun.pgm.dto.request.CreateRoleRequest;
import com.varun.pgm.dto.request.UpdateRoleRequest;
import com.varun.pgm.dto.response.PermissionResponse;
import com.varun.pgm.dto.response.RolePermissionsResponse;
import com.varun.pgm.dto.response.RoleResponse;
import com.varun.pgm.entity.Permission;
import com.varun.pgm.entity.Role;
import com.varun.pgm.repository.PermissionRepository;
import com.varun.pgm.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role name already exists");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIsDefault(false);
        role = roleRepository.save(role);
        return mapToResponse(role);
    }

    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::mapToResponse);
    }

    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return mapToResponse(role);
    }

    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        if (!role.getName().equals(request.getName()) && roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role name already exists");
        }
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role = roleRepository.save(role);
        return mapToResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(role);
    }

    @Transactional
    public void assignPermissionsToRole(Long roleId, AssignPermissionsRequest request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new RuntimeException("Some permissions not found");
        }
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    public RolePermissionsResponse getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<Permission> permissions = role.getPermissions();
        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(this::mapPermissionToResponse)
                .collect(Collectors.toList());
        return new RolePermissionsResponse(permissionResponses);
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new RuntimeException("Permission not found"));
        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    private RoleResponse mapToResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getIsDefault(),
                role.getCreatedAt()
        );
    }

    private PermissionResponse mapPermissionToResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getKey(),
                permission.getName(),
                permission.getDescription(),
                permission.getCreatedAt()
        );
    }
}
