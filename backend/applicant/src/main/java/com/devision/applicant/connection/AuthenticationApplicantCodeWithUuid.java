package com.devision.applicant.connection;

public record AuthenticationApplicantCodeWithUuid(String correlationId, String fullName, String email, String country,
                                                  String city,
                                                  String streetAddress,
                                                  String phoneNumber ) {
}
