package com.devision.applicant.service;

import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.MediaPortfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaService {

    public String uploadProfileImage(MultipartFile file, String applicantId) throws Exception;

    public MediaPortfolio uploadMediaPortfolio(MultipartFile file, String applicantId, String title, String description, Visibility visibility) throws Exception;

    public MediaType determineMediaType(String resourceType);

    public void deleteMedia(String mediaId, String applicantId) throws IOException;
}
