package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
