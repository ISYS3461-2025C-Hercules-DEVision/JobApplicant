package com.devision.applicant.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.model.Resume;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.MediaPortfolioRepository;
import com.devision.applicant.repository.ResumeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService{
    private final Cloudinary cloudinary;
    private final MediaPortfolioRepository mediaRepo;
    private final ApplicantRepository applicantRepo;
    private final ResumeRepository resumeRepo;

    public ImageServiceImpl(Cloudinary cloudinary, MediaPortfolioRepository mediaRepo, ApplicantRepository applicantRepo, ResumeRepository resumeRepo){
        this.cloudinary = cloudinary;
        this.mediaRepo = mediaRepo;
        this.applicantRepo = applicantRepo;
        this.resumeRepo = resumeRepo;
    }

    @Override
    public String uploadProfileImage(MultipartFile file, String applicantId) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "applicants/avatars",
                "public_id", "avatar_" + applicantId,
                "overwrite", true
        ));
        return (String) uploadResult.get("secure_url");
    }

    public MediaPortfolio uploadMediaPortfolio(MultipartFile file,
                                               String resumeId,
                                               String title,
                                               String description,
                                               Visibility visibility) throws Exception{
        String folder = "resumes/" + resumeId + "/portfolio";

        Map params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto" //detects if image or video
        );

        Map result = cloudinary.uploader().upload(file.getBytes(), params);

        Resume r = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        if(r.getMediaPortfolios() == null){
            r.setMediaPortfolios(new ArrayList<>());
        }

        MediaPortfolio media = MediaPortfolio.builder()
                .resumeId(r.getResumeId())
                .fileUrl((String) result.get("secure_url"))
                .publicId((String) result.get("public_id"))
                .mediaType(determineMediaType((String) result.get("resource_type")))
                .title(title)
                .description(description)
                .visibility(visibility)
                .createdAt(Instant.now())
                .build();

        MediaPortfolio savedMedia = mediaRepo.save(media);


        //Add the new media
        r.getMediaPortfolios().add(savedMedia);

        resumeRepo.save(r);
        return savedMedia;
    }

    public MediaType determineMediaType(String resourceType) {
        return "video".equals(resourceType) ? MediaType.VIDEO : MediaType.IMAGE;
    }

    public void deleteMedia(String mediaId, String resumeId) throws IOException {
        MediaPortfolio media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (!media.getResumeId().equals(resumeId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Delete from Cloudinary
        cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());

        // Delete from DB
        Resume r = resumeRepo.findById(resumeId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find resume"));

        if(r.getMediaPortfolios() != null){
            r.getMediaPortfolios().remove(media);
            resumeRepo.save(r);
        }
        mediaRepo.delete(media);


    }
}
