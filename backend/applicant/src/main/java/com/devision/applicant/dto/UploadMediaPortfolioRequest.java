package com.devision.applicant.dto;

import com.devision.applicant.enums.Visibility;
import org.springframework.web.multipart.MultipartFile;

public record UploadMediaPortfolioRequest(MultipartFile file,
                                          String title,
                                          String description,
                                          Visibility visibility) {
}
