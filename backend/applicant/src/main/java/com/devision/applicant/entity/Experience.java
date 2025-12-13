package com.devision.applicant.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {
    @Id
    private String experienceId = UUID.randomUUID().toString(); //primary key

    private String applicantId; //foreign key
    private String jobTitle;
    private String companyName;

    private String fromMonthYear;
    private String toMonthYear;
    private String jobDescription;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
