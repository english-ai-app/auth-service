package com.EnglishApp.auth_service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @Email @NotBlank
        String email,
        @NotBlank @Pattern(regexp = "\\d{6}")
        String otp
) {
}
