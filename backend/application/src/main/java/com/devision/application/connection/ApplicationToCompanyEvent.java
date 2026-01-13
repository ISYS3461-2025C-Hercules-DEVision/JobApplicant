package com.devision.application.connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationToCompanyEvent  extends DtoWithProcessId{
    private String applicationId;
    private String applicantId;
    private List<String> fileUrl;

    public ApplicationToCompanyEvent(String correlationId, String applicationId,  String applicantId, List<String> fileUrl) {
        super(correlationId);
        this.applicationId = applicationId;
        this.applicantId = applicantId;
        this.fileUrl = fileUrl;
    }
}
