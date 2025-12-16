package com.devision.applicant.service;

// ImageService.java (Hypothetical Service Class)
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
// ... imports

public class ImageService {
    private final Cloudinary cloudinary;

    // Constructor Injection
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file, String folder) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        // The raw byte array is what the cloudinary SDK expects
        byte[] fileBytes = file.getBytes();

        // Configuration for the upload
        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder);
        options.put("resource_type", "auto");

        // Execute the upload and get the result map
        Map result = cloudinary.uploader().upload(fileBytes, options);

        // Extract the secure URL from the result
        return (String) result.get("secure_url");
    }
}