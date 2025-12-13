package com.devision.authentication.model;

import com.devision.authentication.enums.AuthProvider;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auth_accounts")
public class AuthAccount {

    @Id
    private String authId;            // UUID

    @Indexed(unique = true)
    private String applicantId;       // UUID (FK-like to Applicant Service)

    @Indexed(unique = true)
    private String email;             // login identifier

    private String passwordHash;      // bcrypt hash (nullable if provider != LOCAL)

    private AuthProvider authProvider; // LOCAL | GOOGLE | FACEBOOK | ...

    private String ssoId;             // nullable (e.g. Google sub)

    private boolean isActivated;

    private Instant createdAt;
    private Instant updatedAt;

    private Integer failedAttempts;   // number
}
