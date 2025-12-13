package com.devision.notification;

import com.devision.notification.model.Notification;
import com.devision.notification.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Configuration
public class InitDataConfig {

    @Bean
    public CommandLineRunner initNotifications(NotificationRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                Notification n = Notification.builder()
                        .notificationId(UUID.randomUUID().toString())
                        .applicantId("test-applicant-123")
                        .type("System")
                        .message("Hello from Notification Service 🎉")
                        .jobPostId(null)
                        .companyId(null)
                        .skillMatches(List.of())
                        .isRead(false)
                        .createdAt(Instant.now())
                        .build();

                repo.save(n);
                System.out.println("Inserted sample notification");
            } else {
                System.out.println("Notifications already exist, skip init.");
            }
        };
    }
}
