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
    private String id;

    private String email;
    private String fullName;
    private String provider;      // "GOOGLE"
    private String providerId;    // Google sub (unique id)

    // For Kafka correlation
    private String correlationId;

    // ID coming back from applicant service
    private String applicantId;
}
