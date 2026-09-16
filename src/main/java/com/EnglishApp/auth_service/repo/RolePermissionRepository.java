package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.RolePermission;
import com.EnglishApp.auth_service.domain.model.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findByIdRoleId(Long roleId);

    List<RolePermission> findByIdPermissionId(Long permissionId);
}
