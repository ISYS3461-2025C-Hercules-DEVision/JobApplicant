package com.devision.authentication.connection;

import com.devision.authentication.dto.common.DtoWithProcessId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthToApplicantEvent extends DtoWithProcessId {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String country;
    private String city;
    private String streetAddress;

    public AuthToApplicantEvent(
            String correlationId,
            String email,
            String fullName,
            String phoneNumber,
            String country,
            String city,
            String streetAddress
    ) {
        super(correlationId);
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.streetAddress = streetAddress;
    }
}
