package com.varun.pgm.service;

import com.varun.pgm.dto.response.AccessCheckResponse;
import com.varun.pgm.dto.response.RoleResponse;
import com.varun.pgm.dto.response.UserRolesResponse;
import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Permission;
import com.varun.pgm.entity.Role;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.entity.UserRoleId;
import com.varun.pgm.repository.AdminRepository;
import com.varun.pgm.repository.RoleRepository;
import com.varun.pgm.repository.StaffRepository;
import com.varun.pgm.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void assignRoleToUser(Long userId, Long roleId, UserRole.UserType userType) {
        // Check if user exists
        if (userType == UserRole.UserType.ADMIN) {
            adminRepository.findById(userId).orElseThrow(() -> new RuntimeException("Admin not found"));
        } else if (userType == UserRole.UserType.STAFF) {
            staffRepository.findById(userId).orElseThrow(() -> new RuntimeException("Staff not found"));
        }

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        UserRoleId id = new UserRoleId(userId, roleId);
        if (userRoleRepository.existsById(id)) {
            throw new RuntimeException("Role already assigned to user");
        }

        UserRole userRole = new UserRole();
        userRole.setId(id);
        userRole.setRole(role);
        userRole.setUserType(userType);
        userRole.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
    }

    public UserRolesResponse getUserRoles(Long userId, UserRole.UserType userType) {
        List<UserRole> userRoles = userRoleRepository.findById_UserIdAndUserType(userId, userType);
        List<RoleResponse> roles = userRoles.stream()
                .map(ur -> new RoleResponse(
                        ur.getRole().getId(),
                        ur.getRole().getName(),
                        ur.getRole().getDescription(),
                        ur.getRole().getIsDefault(),
                        ur.getRole().getCreatedAt()
                ))
                .collect(Collectors.toList());
        return new UserRolesResponse(roles);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId, UserRole.UserType userType) {
        UserRoleId id = new UserRoleId(userId, roleId);
        UserRole userRole = userRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not assigned to user"));
        userRoleRepository.delete(userRole);
    }

    public AccessCheckResponse checkPermission(String permissionKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AccessCheckResponse(false);
        }

        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin == null) {
            return new AccessCheckResponse(false);
        }

        List<UserRole> userRoles = userRoleRepository.findById_UserIdAndUserType(admin.getId(), UserRole.UserType.ADMIN);
        for (UserRole ur : userRoles) {
            List<Permission> permissions = ur.getRole().getPermissions();
            for (Permission p : permissions) {
                if (p.getKey().equals(permissionKey)) {
                    return new AccessCheckResponse(true);
                }
            }
        }

        return new AccessCheckResponse(false);
    }
}
