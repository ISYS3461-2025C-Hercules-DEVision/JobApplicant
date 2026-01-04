package com.devision.authentication.user.entity;

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
    private UserRole role;
    // For Google SSO
    private String provider;      // "LOCAL" or "GOOGLE"
    private String providerId;    // Google "sub" (unique id)
    private String applicantId;
    private String adminId;
}
