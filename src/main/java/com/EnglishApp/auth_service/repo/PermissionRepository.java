package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);
}
