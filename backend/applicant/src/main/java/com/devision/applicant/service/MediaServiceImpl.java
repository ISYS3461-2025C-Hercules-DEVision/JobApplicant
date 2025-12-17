package com.devision.applicant.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.repository.MediaPortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Service
public class MediaServiceImpl implements MediaService{
    private final Cloudinary cloudinary;
    private final MediaPortfolioRepository mediaRepo;

    public MediaServiceImpl(Cloudinary cloudinary, MediaPortfolioRepository mediaRepo){
        this.cloudinary = cloudinary;
        this.mediaRepo = mediaRepo;
    }

    @Override
    public String uploadProfileImage(MultipartFile file, String applicantId) throws Exception{
        Map params = ObjectUtils.asMap(
                "folder", "applicants/" + applicantId + "/avatar",
                "transformation", new Transformation()
                        .width(200).height(200).crop("thumb").gravity("face").radius("max"),
                "overwrite", true,
                "public_id", "avatar"
        );
        Map result = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) result.get("secure_url");
    }

    public MediaPortfolio uploadMediaPortfolio(MultipartFile file,
                                               String applicantId,
                                               String title,
                                               String description,
                                               Visibility visibility) throws Exception{
        String folder = "applicants/" + applicantId + "/portfolio";

        Map params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto" //detects if image or video
        );

        Map result = cloudinary.uploader().upload(file.getBytes(), params);

        MediaPortfolio media = MediaPortfolio.builder()
                .applicantId(applicantId)
                .fileUrl((String) result.get("secure_url"))
                .publicId((String) result.get("public_id"))
                .mediaType(determineMediaType((String) result.get("resource_type")))
                .title(title)
                .description(description)
                .visibility(visibility)
                .createdAt(Instant.now())
                .build();
        return mediaRepo.save(media);
    }

    public MediaType determineMediaType(String resourceType) {
        return "video".equals(resourceType) ? MediaType.VIDEO : MediaType.IMAGE;
    }

    public void deleteMedia(String mediaId, String applicantId) throws IOException {
        MediaPortfolio media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (!media.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Delete from Cloudinary
        cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());

        // Delete from DB
        mediaRepo.delete(media);
    }
}
