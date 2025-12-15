package com.devision.applicant.dto.education;

import com.devision.applicant.model.Education;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record EducationRequestDto(String correlationId, String applicantId, List<Education> educations) implements Serializable {

    public EducationRequestDto(String applicantId, List<Education> educations){
        this(UUID.randomUUID().toString(), applicantId, educations);
    }

    public String getCorrelationId(){
        return correlationId;
    }

    public String getApplicantId(){
        return applicantId;
    }

    public List<Education> getEducations(){
        return educations;
    }
}
