package com.devision.applicant.dto;

import java.time.Instant;
import java.util.List;

public record ProfileUpdateEvent(String applicantId,
                                 String fieldChanged,
                                 Object oldValue,
                                 Object newValue,
                                 Instant timestamp) {
}
