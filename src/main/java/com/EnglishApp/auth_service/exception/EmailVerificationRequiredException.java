package com.EnglishApp.auth_service.exception;

import java.time.LocalDateTime;

public class EmailVerificationRequiredException extends RuntimeException {
    private final String email;
    private final boolean canResend;
    private final LocalDateTime otpExpiresAt;

    public EmailVerificationRequiredException(String email, boolean canResend, LocalDateTime otpExpiresAt, String message) {
        super(message);
        this.email = email;
        this.canResend = canResend;
        this.otpExpiresAt = otpExpiresAt;
    }

    public String getEmail() {
        return email;
    }

    public boolean isCanResend() {
        return canResend;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }
}
