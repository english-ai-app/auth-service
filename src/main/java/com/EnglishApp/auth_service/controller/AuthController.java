package com.EnglishApp.auth_service.controller;

import com.EnglishApp.auth_service.domain.dto.AuthResponse;
import com.EnglishApp.auth_service.domain.dto.LoginRequest;
import com.EnglishApp.auth_service.service.LoginAttemptService;
import com.EnglishApp.auth_service.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            loginAttemptService.loginSucceeded(request.username());
            log.info("login_success username={} ip={}", request.username(), clientIp(httpRequest));
            return tokenService.createToken(authentication, httpRequest);
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(request.username());
            log.warn("login_failed username={} ip={} reason=bad_credentials", request.username(), clientIp(httpRequest));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (LockedException ex) {
            log.warn("login_blocked username={} ip={}", request.username(), clientIp(httpRequest));
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Exceeded login attempt limit");
        }
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void refresh() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Refresh token flow will be added after user persistence is ready");
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
