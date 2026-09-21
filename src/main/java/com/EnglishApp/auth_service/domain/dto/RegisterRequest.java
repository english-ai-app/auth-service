package com.EnglishApp.auth_service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank
        String email,
        String phone,
        @NotBlank @Size(min = 3, max = 64)
        String username,
        @NotBlank @Size(min = 8, max = 72)
        String password,
        String displayName
) {
}
