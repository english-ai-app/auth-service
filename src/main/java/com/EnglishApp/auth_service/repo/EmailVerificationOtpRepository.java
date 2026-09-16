package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.EmailVerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, Long> {
    Optional<EmailVerificationOtp> findFirstByUser_IdAndVerifiedAtIsNullOrderByCreatedAtDesc(Long userId);
}
