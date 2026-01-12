package com.devision.application.dto;

import java.time.Instant;
import java.util.List;

public record CompanyApplicationViewDTO(String applicationId,
                                        String applicantId,
                                        Instant appliedAt,
                                        List<String> fileUrls) {
}
