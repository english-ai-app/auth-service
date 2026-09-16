package com.EnglishApp.auth_service.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class JwkSetEndpoint {
    private final RSAKey rsaKey;

    JwkSetEndpoint(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getKey() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}
