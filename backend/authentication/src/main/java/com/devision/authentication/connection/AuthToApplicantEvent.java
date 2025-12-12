package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AuthToApplicantEvent extends DtoWithProcessId {

    private String authUserId;
    private String email;
    private String fullName;

    public AuthToApplicantEvent() {
    }

    public AuthToApplicantEvent(String correlationId,
                                String authUserId,
                                String email,
                                String fullName) {
        super(correlationId);
        this.authUserId = authUserId;
        this.email = email;
        this.fullName = fullName;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
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
