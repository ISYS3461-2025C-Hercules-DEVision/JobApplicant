package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthToAdminEvent extends DtoWithProcessId {
    private String adminEmail;
    private String adminRole;
    public AuthToAdminEvent(
            String correlationId,
            String adminEmail,
            String role
    ) {
        super(correlationId);
        this.adminEmail = adminEmail;
        this.adminRole = role;

    }
}
