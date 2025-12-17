package com.devision.applicant.model;

import com.devision.applicant.enums.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applicant_skills")
public class ApplicantSkill {
    private String applicantId; // FK-like reference
    private String skillId;

    private ProficiencyLevel proficiency; // Beginner | Intermediate | Advanced
    private List<String> endorsedBy;

    private Instant createdAt;
    private Instant updatedAt;
}
