package com.devision.authentication.dto;

public record RegisterRequest(String email,
                              String password,
                              String fullName,
                              String phoneNumber,
                              String country,
                              String city,
                              String streetAddress) {
}
