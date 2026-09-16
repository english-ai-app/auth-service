package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.UserRole;
import com.EnglishApp.auth_service.domain.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByIdUserId(Long userId);

    List<UserRole> findByIdRoleId(Long roleId);
}
