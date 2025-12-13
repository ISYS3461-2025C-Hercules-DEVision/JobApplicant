package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "educations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Education {
    @Id
    private String educationId = UUID.randomUUID().toString();

    private String applicantId; //foreign key of applicant_id

    private String degree;
    private String institution;

    private Integer fromYear;
    private Integer toYear;

    private Double GPA;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
