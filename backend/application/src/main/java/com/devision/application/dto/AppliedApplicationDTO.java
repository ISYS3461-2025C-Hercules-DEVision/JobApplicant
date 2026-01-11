package com.devision.application.dto;

import java.util.List;

public record AppliedApplicationDTO(String applicationId,
            String applicantId,
        List<String>fileUrl) {
}
