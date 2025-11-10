package com.varun.pgm.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class AdminProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String contactNo;
    private LocalDateTime createdAt;
    private List<String> roles;

    public AdminProfileResponse() {}

    public AdminProfileResponse(Long id, String name, String email, String contactNo, LocalDateTime createdAt, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNo = contactNo;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
