package com.devision.application.model;

import lombok.*;

import java.time.Instant;
import com.devision.application.enums.FileType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "File")
public class FileReference {
    @Id
    private String fileId;        // UUID
    private String applicationId;
    private String fileUrl;       // Cloudinary secure_url
    private String publicId;      // Cloudinary public_id (nên có để delete/update)
    private FileType fileType;    // PDF | DOCX
    private Instant createdAt;
    private Instant updatedAt;
}
