package com.devision.applicant.dto;

import java.time.LocalDateTime;

public record ApplicantForAdmin(String id,
                                String email,
                                String fullName,
                                String Country,
                                LocalDateTime isCreated,
                                Boolean status) {
}
