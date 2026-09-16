package com.EnglishApp.auth_service.domain.dto;

import java.time.LocalDateTime;

public record EmailVerificationRequiredResponse(
        String errorCode,
        String email,
        boolean canResend,
        LocalDateTime otpExpiresAt,
        String message
) {
}
