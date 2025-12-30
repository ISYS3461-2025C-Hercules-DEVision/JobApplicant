package com.devision.application.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.devision.application.api.internal.FileStorageService;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public StoredFile upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folder);
            options.put("resource_type", "auto");

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);

            String publicId = (String) result.get("public_id");
            String secureUrl = (String) result.get("secure_url");

            return new StoredFile(
                publicId,
                secureUrl,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage(), e);
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;

        try {
            cloudinary.uploader().destroy(publicId, Map.of("resource_type", "auto"));
        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage(), e);
        }
    }
}
