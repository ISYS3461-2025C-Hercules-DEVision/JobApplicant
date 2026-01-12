package com.devision.applicant.connection;


import com.devision.applicant.dto.common.common.DtoWithProcessId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplicantToJmEvent extends DtoWithProcessId {

    private String applicantId;
    private String fullName;
    private String country;
    private List<String> skills;
    private Boolean employmentStatus;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;


    public ApplicantToJmEvent(String correlationId,String applicantId, String fullName, String country, List<String> skills, Boolean employmentStatus, BigDecimal minSalary, BigDecimal maxSalary){
        super(correlationId);
        this.applicantId = applicantId;
        this.fullName = fullName;
        this.country = country;
        this.skills = skills;
        this.employmentStatus = employmentStatus;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

}
