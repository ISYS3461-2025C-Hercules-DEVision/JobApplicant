package com.devision.application.connection;

public class ApplicationCompanyCodeWithUuid  extends DtoWithProcessId{
    private String applicationId;


    public ApplicationCompanyCodeWithUuid() {
    }

    public ApplicationCompanyCodeWithUuid(String correlationId, String applicationId) {
        super(correlationId);
        this.applicationId = applicationId;

    }

    public String getAdminId() {
        return applicationId;
    }
}
