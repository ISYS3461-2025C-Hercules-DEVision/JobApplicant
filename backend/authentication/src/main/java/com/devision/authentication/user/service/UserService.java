package com.devision.authentication.user.service;

import com.devision.authentication.dto.LoginRequest;
import com.devision.authentication.dto.RegisterRequest;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.entity.UserRole;
import com.devision.authentication.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.super-admin.email}")
    private String superAdminEmail;
    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    // -------- LOCAL REGISTER --------
    @Transactional
    public User registerLocalUser(RegisterRequest request) {
        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new RuntimeException("Email already in use");
                });

        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .provider("LOCAL")
                .build();

        // correlationId for Kafka
        //user.setCorrelationId(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    // -------- LOCAL LOGIN --------
    @Transactional(readOnly = true)
    public User loginLocalUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!"LOCAL".equals(user.getProvider())) {
            throw new RuntimeException("This account is registered via Google");
        }
        if (user.getRole() != UserRole.USER) {
            throw new RuntimeException("Access denied: User only");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User loginLocalAdmin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        if (!"LOCAL".equals(user.getProvider())) {
            throw new RuntimeException("This account is registered via Google");
        }
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPER_ADMIN) {
            throw new RuntimeException("Access denied: admin only");
        }
        return user;

    }
    // -------- GOOGLE LOGIN/REGISTER --------
    @Transactional
    public User handleGoogleLogin(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name  = (String) attributes.get("name");
        String sub   = (String) attributes.get("sub"); // Google unique id

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .fullName(name)
                        .provider("GOOGLE")
                        .providerId(sub)
                        .build()
                );


        return userRepository.save(user);

    }
    @Transactional
    public void attachApplicantToUser(String userId, String applicantId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    user.setApplicantId(applicantId);
                    userRepository.save(user);
                }, () -> {
                    throw new IllegalStateException("User not found for id=" + userId);
                });
    }
    @Transactional
    public void attachAdminToUser(String userId, String adminId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    user.setAdminId(adminId);
                    userRepository.save(user);
                }, () -> {
                    throw new IllegalStateException("User not found for id=" + userId);
                });
    }
    @Transactional
    public void attachApplicantForAdminToUser(String userId, String applicantForAdminId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    user.setApplicantForAdminId(applicantForAdminId);
                    userRepository.save(user);
                }, () -> {
                    throw new IllegalStateException("User not found for id=" + userId);
                });
    }

}
