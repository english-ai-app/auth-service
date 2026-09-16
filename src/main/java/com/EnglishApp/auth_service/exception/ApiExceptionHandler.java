package com.EnglishApp.auth_service.exception;

import com.EnglishApp.auth_service.domain.dto.ApiResponse;
import com.EnglishApp.auth_service.domain.dto.EmailVerificationRequiredResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EmailVerificationRequiredException.class)
    public ResponseEntity<ApiResponse<EmailVerificationRequiredResponse>> handleEmailVerificationRequired(
            EmailVerificationRequiredException ex
    ) {
        String errorCode = ex.isCanResend() ? "OTP_EXPIRED" : "EMAIL_VERIFICATION_REQUIRED";
        HttpStatus status = ex.isCanResend() ? HttpStatus.GONE : HttpStatus.PRECONDITION_REQUIRED;
        EmailVerificationRequiredResponse data = new EmailVerificationRequiredResponse(
                errorCode,
                ex.getEmail(),
                ex.isCanResend(),
                ex.getOtpExpiresAt(),
                ex.getMessage()
        );

        return ResponseEntity.status(status).body(ApiResponse.error(errorCode, ex.getMessage(), data));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() == null ? status.getReasonPhrase() : ex.getReason();
        return ResponseEntity.status(status).body(ApiResponse.error(status.name(), message, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", message, null));
    }
}
