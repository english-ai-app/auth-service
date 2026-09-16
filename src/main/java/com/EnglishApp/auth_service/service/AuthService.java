package com.EnglishApp.auth_service.service;

import com.EnglishApp.auth_service.domain.dto.AuthResponse;
import com.EnglishApp.auth_service.domain.dto.RegisterRequest;
import com.EnglishApp.auth_service.domain.dto.RegisterResponse;
import com.EnglishApp.auth_service.domain.dto.ResendOtpRequest;
import com.EnglishApp.auth_service.domain.dto.VerifyEmailRequest;
import com.EnglishApp.auth_service.domain.model.EmailVerificationOtp;
import com.EnglishApp.auth_service.domain.model.Role;
import com.EnglishApp.auth_service.domain.model.User;
import com.EnglishApp.auth_service.domain.model.UserRole;
import com.EnglishApp.auth_service.domain.model.UserRoleId;
import com.EnglishApp.auth_service.exception.EmailVerificationRequiredException;
import com.EnglishApp.auth_service.repo.RoleRepository;
import com.EnglishApp.auth_service.repo.UserRepository;
import com.EnglishApp.auth_service.repo.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailOtpService emailOtpService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = User.builder()
                .email(email)
                .phone(blankToNull(request.phone()))
                .username(username)
                .displayName(blankToNull(request.displayName()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .status((byte) 1)
                .build();
        User savedUser = userRepository.save(user);

        Role userRole = roleRepository.findByCode(DEFAULT_ROLE)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .code(DEFAULT_ROLE)
                        .name("User")
                        .description("Default application user")
                        .build()));
        userRoleRepository.save(UserRole.builder()
                .id(new UserRoleId(savedUser.getId(), userRole.getId()))
                .user(savedUser)
                .role(userRole)
                .build());

        EmailVerificationOtp otp = emailOtpService.createAndSendOtp(savedUser);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), true, otp.getExpiresAt(), "OTP sent to email");
    }

    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest) {
        User user = getUserByEmail(request.email());
        if (user.getEmailVerifiedAt() != null) {
            return tokenService.createTokenForUser(user.getUsername(), List.of("ROLE_" + DEFAULT_ROLE), httpRequest);
        }

        if (!emailOtpService.verify(user, request.otp())) {
            EmailVerificationOtp otp = emailOtpService.getLatestPendingOtp(user);
            boolean expired = emailOtpService.isExpired(otp);
            throw new EmailVerificationRequiredException(
                    user.getEmail(),
                    expired,
                    otp == null ? null : otp.getExpiresAt(),
                    expired ? "OTP expired" : "Invalid OTP"
            );
        }

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
        return tokenService.createTokenForUser(user.getUsername(), List.of("ROLE_" + DEFAULT_ROLE), httpRequest);
    }

    @Transactional
    public RegisterResponse resendOtp(ResendOtpRequest request) {
        User user = getUserByEmail(request.email());
        if (user.getEmailVerifiedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already verified");
        }

        EmailVerificationOtp otp = emailOtpService.createAndSendOtp(user);
        return new RegisterResponse(user.getId(), user.getEmail(), true, otp.getExpiresAt(), "OTP resent to email");
    }

    @Transactional(readOnly = true)
    public void ensureEmailVerified(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(normalizeEmail(usernameOrEmail)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (user.getEmailVerifiedAt() != null) {
            return;
        }

        EmailVerificationOtp otp = emailOtpService.getLatestPendingOtp(user);
        boolean expired = emailOtpService.isExpired(otp);
        throw new EmailVerificationRequiredException(
                user.getEmail(),
                expired,
                otp == null ? null : otp.getExpiresAt(),
                expired ? "OTP expired. Please resend verification code." : "Email verification required"
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
