package com.devision.authorization;

import com.devision.authorization.enums.TokenType;
import com.devision.authorization.model.AuthToken;
import com.devision.authorization.repository.AuthTokenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class AuthTokenDataSeeder implements CommandLineRunner {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenDataSeeder(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    public void run(String... args) {
        if (authTokenRepository.count() > 0) return;

        String authId = UUID.randomUUID().toString();

        AuthToken access = AuthToken.builder()
                .tokenId(UUID.randomUUID().toString())
                .authId(authId)
                .token("access_" + UUID.randomUUID())
                .tokenType(TokenType.ACCESS)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .isRevoked(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        AuthToken refresh = AuthToken.builder()
                .tokenId(UUID.randomUUID().toString())
                .authId(authId)
                .token("refresh_" + UUID.randomUUID())
                .tokenType(TokenType.REFRESH)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .isRevoked(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        authTokenRepository.save(access);
        authTokenRepository.save(refresh);

        System.out.println("Seeded tokens for authId=" + authId);
    }
}
