package com.anilaltunkan.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Token {
    private TokenType tokenType;
    private String tokenValue;
    private Long duration;

    public Token(TokenType access, String token, Long duration, LocalDateTime ofInstant) {
        this.tokenType = access;
        this.tokenValue = token;
        this.duration = duration;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    private LocalDateTime expiryDate;

    public enum TokenType {
        ACCESS, REFRESH
    }
}