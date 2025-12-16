package com.devision.applicant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.devision.applicant.enums.ProficiencyLevel;


import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applicant_skills")
public class ApplicantSkill {

    @Id
    private String id; // UUID of this skill record

    private String applicantId; // FK-like reference
    private String skillId;

    private ProficiencyLevel proficiency; // Beginner | Intermediate | Advanced
    private List<String> endorsedBy;

    private Instant createdAt;
    private Instant updatedAt;
}
