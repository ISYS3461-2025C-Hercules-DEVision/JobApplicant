package com.devision.authentication;

import com.devision.authentication.enums.AuthProvider;
import com.devision.authentication.model.AuthAccount;
import com.devision.authentication.repository.AuthAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class AuthAccountDataSeeder implements CommandLineRunner {

    private final AuthAccountRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "test@applicant.com";

        if (repo.existsByEmail(email)) {
            System.out.println("[Seeder] Test auth account already exists -> " + email);
            return;
        }

        String authId = UUID.randomUUID().toString();
        String applicantId = UUID.randomUUID().toString();

        AuthAccount account = AuthAccount.builder()
                .authId(authId)
                .applicantId(applicantId)
                .email(email)
                .passwordHash(passwordEncoder.encode("Password@123"))
                .authProvider(AuthProvider.LOCAL)
                .ssoId(null)
                .isActivated(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .failedAttempts(0)
                .build();

        repo.save(account);

        System.out.println("[Seeder] Inserted test auth account:");
        System.out.println("  authId      = " + authId);
        System.out.println("  applicantId = " + applicantId);
        System.out.println("  email       = " + email);
        System.out.println("  password    = Password@123");
    }
}
