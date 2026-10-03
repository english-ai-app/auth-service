package com.EnglishApp.auth_service.domain.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        Long userId,
        List<String> roles
) {
}
