package com.example.application.application.dto;



import com.example.application.application.model.FileReference;

import java.util.List;

public record ApplicationCreateRequest(String applicantId,
                                       String jobPostId,
                                       String companyId,
                                       List<FileReference> documents) {
}
