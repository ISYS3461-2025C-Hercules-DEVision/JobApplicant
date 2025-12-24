package com.devision.applicant.connection;


import com.devision.applicant.dto.common.common.DtoWithProcessId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplicantToJmDescDto extends DtoWithProcessId {

    private String country;
    private List<String> skills;

    public ApplicantToJmDescDto(String correlationId, String country, List<String> skills){
        super(correlationId);
        this.country = country;
        this.skills = skills;
    }

}
