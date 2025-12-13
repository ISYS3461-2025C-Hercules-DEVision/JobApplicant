package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Applicant {
    @Id
    private String applicantId = UUID.randomUUID().toString();

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password; //store hash password

    private String phone;
    private String streetAddress;
    private String city;
    private String country;
    private Role role;

    private String profileImageURL;
    private Boolean isActivated = false;
    private Boolean isArchived = false;
    private String objectiveSummary;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant deletedAt;

}
