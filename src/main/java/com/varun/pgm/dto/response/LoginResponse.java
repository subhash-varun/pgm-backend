package com.varun.pgm.dto.response;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;

    public LoginResponse() {}

    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
