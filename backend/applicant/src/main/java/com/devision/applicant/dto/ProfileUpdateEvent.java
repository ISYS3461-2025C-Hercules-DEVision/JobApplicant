package com.devision.applicant.dto;

import java.time.Instant;

public record ProfileUpdateEvent(String applicantId,
                                 String fileChanged,
                                 Object oldValue,
                                 Object newValue,
                                 Instant timestamp) {
}
