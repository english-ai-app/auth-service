package com.EnglishApp.auth_service.repo;

import com.EnglishApp.auth_service.domain.model.OauthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {
    Optional<OauthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    List<OauthAccount> findByUser_Id(Long userId);
}
