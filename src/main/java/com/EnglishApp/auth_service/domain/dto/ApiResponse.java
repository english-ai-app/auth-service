package com.EnglishApp.auth_service.domain.dto;

public record ApiResponse<T>(
        String code,
        boolean success,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", true, message, data);
    }

    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(code, false, message, data);
    }
}
