package com.devision.applicant.model;

import com.devision.applicant.enums.MediaType;
import com.devision.applicant.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "media_portfolios")
public class MediaPortfolio {
    @Id
    private String mediaId;

    private String resumeId;

    private String fileUrl;      // Cloudinary secure_url
    private String publicId;     // Cloudinary public_id (VERY IMPORTANT)

    private MediaType mediaType;    // image | video | link
    private String title;
    private String description;

    private Visibility visibility;   // public | private
    private Instant createdAt;
}
