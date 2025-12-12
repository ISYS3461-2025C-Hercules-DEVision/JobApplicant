package com.devision.authentication.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    private String email;
    private String fullName;

    // For local login
    private String password;      // hashed (BCrypt)

    // For Google SSO
    private String provider;      // "LOCAL" or "GOOGLE"
    private String providerId;    // Google "sub" (unique id)

    // Kafka correlation + link to applicant
    private String correlationId;
    private String applicantId;
}
