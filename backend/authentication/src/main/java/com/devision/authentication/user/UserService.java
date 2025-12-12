package com.devision.authentication.user;

import com.devision.authentication.dto.LoginRequest;
import com.devision.authentication.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setCorrelationId(UUID.randomUUID().toString());

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

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
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

        // new correlationId for each Google login/registration flow
        user.setCorrelationId(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    @Transactional
    public void attachApplicantToUser(String correlationId, String applicantId) {
        userRepository.findByCorrelationId(correlationId)
                .ifPresent(user -> {
                    user.setApplicantId(applicantId);
                    userRepository.save(user);
                });
    }
}
