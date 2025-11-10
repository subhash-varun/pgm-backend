package com.varun.pgm.service;

import com.varun.pgm.dto.request.CreatePermissionRequest;
import com.varun.pgm.dto.request.UpdatePermissionRequest;
import com.varun.pgm.dto.response.PermissionResponse;
import com.varun.pgm.entity.Permission;
import com.varun.pgm.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByKey(request.getKey()).isPresent()) {
            throw new RuntimeException("Permission key already exists");
        }
        Permission permission = new Permission();
        permission.setKey(request.getKey());
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission = permissionRepository.save(permission);
        return mapToResponse(permission);
    }

    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(this::mapToResponse);
    }

    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
        return mapToResponse(permission);
    }

    public PermissionResponse updatePermission(Long id, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
        if (!permission.getKey().equals(request.getKey()) && permissionRepository.findByKey(request.getKey()).isPresent()) {
            throw new RuntimeException("Permission key already exists");
        }
        permission.setKey(request.getKey());
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission = permissionRepository.save(permission);
        return mapToResponse(permission);
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
        permissionRepository.delete(permission);
    }

    private PermissionResponse mapToResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getKey(),
                permission.getName(),
                permission.getDescription(),
                permission.getCreatedAt()
        );
    }
}
