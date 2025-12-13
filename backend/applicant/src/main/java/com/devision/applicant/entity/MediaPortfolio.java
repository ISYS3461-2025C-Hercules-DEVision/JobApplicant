package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "media_portfolios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaPortfolio {
    @Id
    private String mediaId = UUID.randomUUID().toString();

    private String applicantId; //FK to applicant
    private String fileUrl;
    private MediaType mediaType;
    private String title;
    private String description;
    private Visibility visibility;

    @CreatedDate
    private Instant createdAt;
}
