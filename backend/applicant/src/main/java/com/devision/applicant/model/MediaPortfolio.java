package com.devision.applicant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media_portfolios")
public class MediaPortfolio {

    @Id
    private String mediaId;

    private String applicantId;

    private String fileUrl;      // Cloudinary secure_url
    private String publicId;     // Cloudinary public_id (VERY IMPORTANT)

    private String mediaType;    // image | video | link
    private String title;
    private String description;

    private String visibility;   // public | private

    private Instant createdAt;
}

