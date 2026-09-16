package com.EnglishApp.auth_service.domain.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long userId,
        String email,
        boolean otpRequired,
        LocalDateTime otpExpiresAt,
        String message
) {
}
