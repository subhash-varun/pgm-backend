package com.varun.pgm.service;

import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Permission;
import com.varun.pgm.entity.Staff;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.repository.AdminRepository;
import com.varun.pgm.repository.StaffRepository;
import com.varun.pgm.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find user as Admin first
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return createUserDetails(admin.getEmail(), admin.getPassword(), getPermissionsForUser(admin.getId(), UserRole.UserType.ADMIN));
        }

        // Try to find user as Staff
        Staff staff = staffRepository.findByEmail(email).orElse(null);
        if (staff != null) {
            return createUserDetails(staff.getEmail(), staff.getPassword(), getPermissionsForUser(staff.getId(), UserRole.UserType.STAFF));
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    private UserDetails createUserDetails(String email, String password, Set<String> permissions) {
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.builder()
                .username(email)
                .password(password)
                .authorities(authorities)
                .build();
    }

    private Set<String> getPermissionsForUser(Long userId, UserRole.UserType userType) {
        List<Permission> permissions = userRoleRepository.findPermissionsByUserIdAndUserType(userId, userType);
        return permissions.stream()
                .map(Permission::getKey)
                .collect(Collectors.toSet());
    }
}
