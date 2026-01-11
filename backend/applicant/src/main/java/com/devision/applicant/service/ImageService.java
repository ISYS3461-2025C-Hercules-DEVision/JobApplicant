package com.devision.applicant.service;

import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.MediaPortfolio;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    public String uploadProfileImage(MultipartFile file, String applicantId) throws Exception;

    public MediaPortfolio uploadMediaPortfolio(MultipartFile file, String resumeId, String title, String description, Visibility visibility) throws Exception;

    public MediaType determineMediaType(String resourceType);

    public void deleteMedia(String mediaId, String resumeId) throws IOException;
}
