package com.devision.authentication.dto;

public record AuthCookieResponse(        String accessToken,
                                         String userId,
                                         String applicantId,
                                         String email,
                                         String fullName,
                                         Boolean status) {
}
