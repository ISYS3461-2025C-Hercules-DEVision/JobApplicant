package com.devision.applicant.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "applicants")
@CompoundIndex(name = "country_shard_idx", def = "{'country': 1}")
public class Applicant {

    @Field(name = "applicantId")
    private String applicantId = UUID.randomUUID().toString();   // PK, UUID

    @Field(name = "fullName")
    private String fullName;   // max 200 (enforce in DTO or validation)

    @Indexed(unique = true)
    @Field(name = "email")
    private String email;      // UNIQUE

    @Field(name = "password")
    private String password; //store hashed password

    @Field(name = "country")
    private String country;    // shard key

    @Field(name = "city")
    private String city;

    @Field(name = "streetAddress")
    private String streetAddress;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "objectiveSummary")
    private String objectiveSummary;

    @Field(name = "profileImageUrl")
    private String profileImageUrl;

    @Field(name = "isActivated")
    @Builder.Default
    private Boolean isActivated = false;

    @Field(name = "isArchived")
    @Builder.Default
    private Boolean isArchived = false;

    private List<Education> educations = new ArrayList<>();
    private List<WorkExperience> experiences = new ArrayList<>();

    private List<String> skills = new ArrayList<>();
    private List<MediaPortfolio> mediaPortfolios = new ArrayList<>();

    @CreatedDate
    @Field(name = "createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Field(name = "deletedAt")
    private LocalDateTime deletedAt;   // null = not deleted
}
