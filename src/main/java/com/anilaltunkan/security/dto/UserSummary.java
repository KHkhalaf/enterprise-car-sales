package com.anilaltunkan.security.dto;

import lombok.Data;


@Data
public class UserSummary {
    private Long userId;
    private String email;

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
