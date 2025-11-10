package com.varun.pgm.service;

import com.varun.pgm.entity.Admin;
import com.varun.pgm.entity.Staff;
import com.varun.pgm.repository.AdminRepository;
import com.varun.pgm.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Staff createStaff(Staff staff) {
        // Validate admin exists
        if (staff.getAdmin() != null && staff.getAdmin().getId() != null) {
            Admin admin = adminRepository.findById(staff.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            staff.setAdmin(admin);
        }

        if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }
        staff.setCreatedAt(LocalDateTime.now());
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Page<Staff> getAllStaff(Pageable pageable) {
        return staffRepository.findAll(pageable);
    }

    public List<Staff> getStaffByAdminId(Long adminId) {
        return staffRepository.findByAdminId(adminId);
    }

    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }

    public Optional<Staff> getStaffByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    public Staff updateStaff(Long id, Staff staffDetails) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setName(staffDetails.getName());
        staff.setEmail(staffDetails.getEmail());
        if (staffDetails.getPassword() != null && !staffDetails.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(staffDetails.getPassword()));
        }
        staff.setRole(staffDetails.getRole());
        staff.setStatus(staffDetails.getStatus());

        // Update admin if provided
        if (staffDetails.getAdmin() != null && staffDetails.getAdmin().getId() != null) {
            Admin admin = adminRepository.findById(staffDetails.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            staff.setAdmin(admin);
        }

        return staffRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staffRepository.delete(staff);
    }
}
