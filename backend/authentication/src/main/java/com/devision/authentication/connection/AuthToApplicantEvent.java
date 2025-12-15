package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AuthToApplicantEvent extends DtoWithProcessId {

    private String email;
    private String fullName;

    public AuthToApplicantEvent() {
    }

    public AuthToApplicantEvent(
                                String email,
                                String fullName,
                                String correlationId) {
        super(correlationId);

        this.email = email;
        this.fullName = fullName;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
