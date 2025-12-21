package com.devision.applicant.dto;

import org.springframework.web.multipart.MultipartFile;

public record UploadAvatarRequest(MultipartFile file) {
}
