package com.devision.applicant.connection;


import com.devision.applicant.dto.common.common.DtoWithProcessId;

import java.util.List;

public class ApplicantDescDto extends DtoWithProcessId {
    private String country;
    private List<String> skills;

    public ApplicantDescDto(){}

    public ApplicantDescDto(String correlationId, String country, List<String> skills){
        super(correlationId);
        this.country = country;
        this.skills = skills;
    }

    public String getCountry(){
        return country;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public List<String> getSkills(){
        return skills;
    }

    public void setSkills(List<String> skills){
        this.skills = skills;
    }
}
