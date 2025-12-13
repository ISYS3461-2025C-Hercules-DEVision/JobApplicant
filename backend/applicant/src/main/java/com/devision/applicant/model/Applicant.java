package com.devision.applicant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applicants")
public class Applicant {

    @Id
    private String applicantId; // UUID

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String country;
    private String city;

    private String streetAddress;
    private String phoneNumber;
    private String profileImageUrl;

    private boolean isActivated;
    private boolean isArchived;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
