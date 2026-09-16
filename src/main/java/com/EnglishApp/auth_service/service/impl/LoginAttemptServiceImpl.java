package com.EnglishApp.auth_service.service.impl;

import com.EnglishApp.auth_service.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private static final long BLOCK_WINDOW_SECONDS = 600L;
    private final ConcurrentMap<String, AttemptCounter> attempts = new ConcurrentHashMap<>();

    @Value("${maxFailedLoginAttempt:5}")
    private int maxAttempt;

    @Override
    public void loginSucceeded(String key) {
        attempts.remove(normalize(key));
    }

    @Override
    public void loginFailed(String key) {
        attempts.compute(normalize(key), (username, counter) -> {
            if (counter == null || counter.isExpired()) {
                return new AttemptCounter(1, Instant.now().plusSeconds(BLOCK_WINDOW_SECONDS));
            }
            return counter.incremented();
        });
    }

    @Override
    public boolean isBlocked(String key) {
        AttemptCounter counter = attempts.get(normalize(key));
        if (counter == null || counter.isExpired()) {
            attempts.remove(normalize(key));
            return false;
        }
        return counter.count() >= maxAttempt;
    }

    private String normalize(String key) {
        return key == null ? "" : key.toUpperCase(Locale.ROOT);
    }

    private record AttemptCounter(int count, Instant expiresAt) {
        private boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        private AttemptCounter incremented() {
            return new AttemptCounter(count + 1, expiresAt);
        }
    }
}
