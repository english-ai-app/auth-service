package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.OauthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthClientRepository extends JpaRepository<OauthClient, Long> {
    Optional<OauthClient> findByClientId(String clientId);

    boolean existsByClientId(String clientId);
}
