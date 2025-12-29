package com.devdivision.internal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("ApplicantForAdmin")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantForAdmin {
    @Id
    private String id;
  // max 200 (enforce in DTO or validation)

    @Indexed(unique = true)
    @Field(name = "email")
    private String email;      // UNIQUE

    @Field(name = "fullName")
    private String fullName;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "country")
    private String country;// shard key
    @Builder.Default
    @Field(name = "isActivated")
    private Boolean isActivated = true;
}
