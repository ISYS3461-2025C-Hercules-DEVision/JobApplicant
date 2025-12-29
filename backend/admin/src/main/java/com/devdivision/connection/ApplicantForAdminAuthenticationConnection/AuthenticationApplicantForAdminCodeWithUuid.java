package com.devdivision.connection.ApplicantForAdminAuthenticationConnection;

public record AuthenticationApplicantForAdminCodeWithUuid(String correlationId,
                                                          String email,
                                                          String fullName,
                                                          String phoneNumber,
                                                          String country) {
}
