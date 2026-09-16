package com.EnglishApp.auth_service.service;

import com.EnglishApp.auth_service.domain.model.User;
import com.EnglishApp.auth_service.repo.UserRepository;
import com.EnglishApp.auth_service.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private static final int STATUS_ACTIVE = 1;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String normalized = usernameOrEmail == null ? "" : usernameOrEmail.trim();
        User user = userRepository.findByUsername(normalized)
                .or(() -> userRepository.findByEmail(normalized.toLowerCase()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = userRoleRepository.findByIdUserId(user.getId()).stream()
                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getCode()))
                .toList();

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPasswordHash() == null ? "{noop}oauth-only-account" : user.getPasswordHash())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(user.getStatus() == null || user.getStatus() != STATUS_ACTIVE)
                .build();
    }
}
