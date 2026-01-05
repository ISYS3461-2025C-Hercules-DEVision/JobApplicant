package com.devision.application.dto;

import com.devision.application.model.FileReference;

import java.util.List;

public record ApplicationCreateRequest(String applicantId,
                                       String jobPostId,
                                       String companyId,
                                       List<FileReference> documents) {
}
