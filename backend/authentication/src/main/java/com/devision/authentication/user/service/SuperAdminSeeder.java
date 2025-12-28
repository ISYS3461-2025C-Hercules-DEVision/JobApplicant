package com.devision.authentication.user.service;

import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.entity.UserRole;
import com.devision.authentication.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.super-admin.email}")
    private String superAdminEmail;
    @Value("${app.super-admin.password}")
    private String superAdminPassword;
    public SuperAdminSeeder(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if(superAdminEmail == null || superAdminEmail.isBlank() || superAdminPassword == null || superAdminPassword.isBlank()) {
            throw new RuntimeException("SuperAdmin email or password is blank");
        }
        boolean userExists = userRepository.existsByRole(UserRole.SUPER_ADMIN);
        if(userExists) {
            return;
        }
        User superAdmin = User.builder()
                .email(superAdminEmail)
                .fullName("Super Admin")
                .password (passwordEncoder.encode(superAdminPassword))
                .provider("LOCAL")
                .role(UserRole.SUPER_ADMIN)
                .build();
        userRepository.save(superAdmin);
        System.out.println("Super Admin has been seeded: " + superAdminEmail);
    }
}
