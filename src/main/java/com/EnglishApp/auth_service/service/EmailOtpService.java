package com.EnglishApp.auth_service.service;

import com.EnglishApp.auth_service.domain.model.EmailVerificationOtp;
import com.EnglishApp.auth_service.domain.model.User;
import com.EnglishApp.auth_service.repo.EmailVerificationOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailOtpService {
    private static final int OTP_TTL_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Transactional
    public EmailVerificationOtp createAndSendOtp(User user) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        EmailVerificationOtp verificationOtp = EmailVerificationOtp.builder()
                .user(user)
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                .build();

        EmailVerificationOtp savedOtp = otpRepository.save(verificationOtp);
        mailService.sendVerificationOtp(user.getEmail(), otp);
        return savedOtp;
    }

    @Transactional(readOnly = true)
    public EmailVerificationOtp getLatestPendingOtp(User user) {
        return otpRepository.findFirstByUser_IdAndVerifiedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElse(null);
    }

    public boolean isExpired(EmailVerificationOtp otp) {
        return otp == null || otp.getExpiresAt().isBefore(LocalDateTime.now());
    }

    @Transactional
    public boolean verify(User user, String rawOtp) {
        EmailVerificationOtp otp = getLatestPendingOtp(user);
        if (isExpired(otp)) {
            return false;
        }

        if (!passwordEncoder.matches(rawOtp, otp.getOtpHash())) {
            return false;
        }

        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);
        return true;
    }
}
