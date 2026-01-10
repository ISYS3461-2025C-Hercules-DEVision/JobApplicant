package com.devision.application.controller;

import com.devision.application.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinaryService.uploadPdf(file);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Upload success",
                        "url", uploadResult.get("secure_url"),
                        "publicId", uploadResult.get("public_id"),
                        "resourceType", uploadResult.get("resource_type")
                )
        );
    }

}
