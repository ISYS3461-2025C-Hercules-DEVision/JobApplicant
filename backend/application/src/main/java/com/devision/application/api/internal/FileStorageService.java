package com.devision.application.api.internal;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile upload(MultipartFile file, String folder);
    void delete(String publicId);

    record StoredFile(
            String publicId,
            String url,
            String originalFileName,
            String contentType,
            long sizeBytes
    ) {}
}
