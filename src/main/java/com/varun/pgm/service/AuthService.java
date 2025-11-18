package com.varun.pgm.service;

import com.varun.pgm.dto.request.LoginRequest;
import com.varun.pgm.dto.request.RegisterRequest;
import com.varun.pgm.dto.request.UpdateProfileRequest;
import com.varun.pgm.dto.response.AdminProfileResponse;
import com.varun.pgm.dto.response.LoginResponse;
import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Staff;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.repository.AdminRepository;
import com.varun.pgm.repository.StaffRepository;
import com.varun.pgm.repository.TenantRepository;
import com.varun.pgm.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Try to find user as Admin first
        Admin admin = adminRepository.findByEmail(request.getEmail()).orElse(null);
        if (admin != null) {
            return new LoginResponse(jwtUtil.generateToken(admin.getEmail()), 86400000L); // expires in 24 hours
        }

        // Try to find user as Staff
        Staff staff = staffRepository.findByEmail(request.getEmail()).orElse(null);
        if (staff != null) {
            return new LoginResponse(jwtUtil.generateToken(staff.getEmail()), 86400000L); // expires in 24 hours
        }

        // Try to find user as Tenant
        Tenant tenant = tenantRepository.findByEmail(request.getEmail()).orElse(null);
        if (tenant != null) {
            return new LoginResponse(jwtUtil.generateToken(tenant.getEmail()), 86400000L); // expires in 24 hours
        }

        throw new RuntimeException("User not found");
    }

    public Admin register(RegisterRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setContactNo(request.getContactNo());
        admin.setCreatedAt(LocalDateTime.now());

        return adminRepository.save(admin);
    }

    public AdminProfileResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Try to find user as Admin first
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            List<String> roles = getUserRoles(admin.getId(), UserRole.UserType.ADMIN);
            return new AdminProfileResponse(
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getContactNo(),
                    admin.getCreatedAt(),
                    roles
            );
        }

        // Try to find user as Staff
        Staff staff = staffRepository.findByEmail(email).orElse(null);
        if (staff != null) {
            List<String> roles = getUserRoles(staff.getId(), UserRole.UserType.STAFF);
            return new AdminProfileResponse(
                    staff.getId(),
                    staff.getName(),
                    staff.getEmail(),
                    null, // Staff doesn't have contactNo field
                    staff.getCreatedAt(),
                    roles
            );
        }

        // Try to find user as Tenant
        Tenant tenant = tenantRepository.findByEmail(email).orElse(null);
        if (tenant != null) {
            List<String> roles = getUserRoles(tenant.getId(), UserRole.UserType.TENANT);
            return new AdminProfileResponse(
                    tenant.getId(),
                    tenant.getName(),
                    tenant.getEmail(),
                    tenant.getPhone(), // Use phone as contact
                    tenant.getCreatedAt(),
                    roles
            );
        }

        throw new RuntimeException("User not found with email: " + email);
    }

    public AdminProfileResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow();
        admin.setName(request.getName());
        admin.setContactNo(request.getContactNo());
        adminRepository.save(admin);
        return getProfile();
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    private List<String> getUserRoles(Long userId, UserRole.UserType userType) {
        List<UserRole> userRoles = userRoleRepository.findById_UserIdAndUserType(userId, userType);
        return userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
    }
}
