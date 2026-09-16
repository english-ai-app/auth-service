package com.EnglishApp.auth_service.service;

import com.EnglishApp.auth_service.common.Constants;
import com.EnglishApp.auth_service.configuration.OauthProperties;
import com.EnglishApp.auth_service.domain.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final OauthProperties oauthProperties;

    public AuthResponse createToken(Authentication authentication, HttpServletRequest request) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return createTokenForUser(username, roles, request);
    }

    public AuthResponse createTokenForUser(String username, List<String> roles, HttpServletRequest request) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(oauthProperties.getDefaultAccessTokenTimeout());

        Map<String, Object> actionUser = Map.of(
                "staffCode", username,
                "ipAddress", clientIp(request),
                "systemType", request.getHeader("X-System-Type") == null ? "ENGLISH_APP" : request.getHeader("X-System-Type")
        );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("scope", String.join(" ", roles))
                .claim("roles", roles)
                .claim(Constants.ACTION_USER, actionUser)
                .claim(Constants.TENANT, request.getHeader("X-Tenant") == null ? "default" : request.getHeader("X-Tenant"))
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new AuthResponse(accessToken, "Bearer", oauthProperties.getDefaultAccessTokenTimeout(), username, roles);
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
