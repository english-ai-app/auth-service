package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);
}
