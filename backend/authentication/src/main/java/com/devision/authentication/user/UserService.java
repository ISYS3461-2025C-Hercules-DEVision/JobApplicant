package com.devision.authentication.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Called when Google login succeeds.
     * Returns the User and sets a fresh correlationId for Kafka.
     */
    @Transactional
    public User handleGoogleLogin(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String sub = (String) attributes.get("sub"); // Google unique id

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .fullName(name)
                        .provider("GOOGLE")
                        .providerId(sub)
                        .build()
                );

        // Generate a new correlationId for this login flow
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
