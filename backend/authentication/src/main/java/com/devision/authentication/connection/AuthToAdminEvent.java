package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;

public class AuthToAdminEvent extends DtoWithProcessId {
    private String adminEmail;
    public AuthToAdminEvent(
            String correlationId,
            String adminEmail

    ) {
        super(correlationId);
        this.adminEmail = adminEmail;

    }
}
