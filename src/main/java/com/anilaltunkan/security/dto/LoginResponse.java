package com.anilaltunkan.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class LoginResponse {
    public LoginResponse(SuccessFailure status, String message) {
        this.status = status;
        this.message = message;
    }

    public SuccessFailure getStatus() {
        return status;
    }

    public void setStatus(SuccessFailure status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private SuccessFailure status;
    private String message;

    public enum SuccessFailure {
        SUCCESS, FAILURE
    }
}
