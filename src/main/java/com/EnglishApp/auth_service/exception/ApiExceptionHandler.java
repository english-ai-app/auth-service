package com.EnglishApp.auth_service.exception;

import com.EnglishApp.auth_service.domain.dto.EmailVerificationRequiredResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EmailVerificationRequiredException.class)
    public ResponseEntity<EmailVerificationRequiredResponse> handleEmailVerificationRequired(
            EmailVerificationRequiredException ex
    ) {
        String errorCode = ex.isCanResend() ? "OTP_EXPIRED" : "EMAIL_VERIFICATION_REQUIRED";
        HttpStatus status = ex.isCanResend() ? HttpStatus.GONE : HttpStatus.PRECONDITION_REQUIRED;

        return ResponseEntity.status(status).body(new EmailVerificationRequiredResponse(
                errorCode,
                ex.getEmail(),
                ex.isCanResend(),
                ex.getOtpExpiresAt(),
                ex.getMessage()
        ));
    }
}
