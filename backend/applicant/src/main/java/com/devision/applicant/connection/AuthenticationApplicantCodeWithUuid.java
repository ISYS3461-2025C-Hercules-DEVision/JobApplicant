package com.devision.applicant.connection;

public record AuthenticationApplicantCodeWithUuid(String correlationId,
                                                  String email,
                                                  String fullName,
                                                  String phoneNumber,
                                                  String country,
                                                  String city,
                                                  String streetAddress ) {
}
