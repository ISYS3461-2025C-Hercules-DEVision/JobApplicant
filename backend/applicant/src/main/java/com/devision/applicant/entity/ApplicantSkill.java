package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "applicant_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSkill {
    @Id
    private String applicantSkillId = UUID.randomUUID().toString(); //primary key

    private String applicantId; //foreign key
    private String skillId;
    private String skillName;
    private Proficiency proficiency;
    private Integer yearsOfExperience;

    @Builder.Default
    private List<String> endorsedBy = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
