package com.devision.application.model;

import lombok.*;

import java.time.Instant;
import com.devision.application.enums.FileType;           



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileReference {

    private String fileId;        // UUID
    private String applicationId;
    private String fileUrl;       // Cloudinary secure_url
    private String publicId;      // Cloudinary public_id (nên có để delete/update)
    private FileType fileType;    // PDF | DOCX
    private Instant createdAt;
    private Instant updatedAt;
}
