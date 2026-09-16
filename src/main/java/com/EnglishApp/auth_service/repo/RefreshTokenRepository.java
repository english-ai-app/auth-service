package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUser_Id(Long userId);

    List<RefreshToken> findByExpiresAtBefore(LocalDateTime expiresAt);
}
