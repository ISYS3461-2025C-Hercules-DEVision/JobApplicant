package com.devision.application.api.external;

import com.devision.application.dto.external.request.CreateApplicationRequest;
import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Applicant Applications")
@RequestMapping("/api/v1/applications")
public interface ApplicantApplicationApi {

    /**
     * Apply for a job post (applicant only).
     * Identity comes from Gateway headers.
     */
    @PostMapping
    ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody CreateApplicationRequest request);

    /**
     * List my applications (/me).
     */
    @GetMapping("/me")
    ResponseEntity<List<ApplicationSummaryResponse>> listMyApplications();

    /**
     * Get my application by id.
     */
    @GetMapping("/me/{applicationId}")
    ResponseEntity<ApplicationResponse> getMyApplication(@PathVariable String applicationId);

    /**
     * Upload CV file for my application.
     */
    @PostMapping(value = "/me/{applicationId}/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApplicationResponse> uploadCv(
            @PathVariable String applicationId,
            @RequestPart("file") MultipartFile file
    );

    /**
     * Upload cover letter file for my application.
     */
    @PostMapping(value = "/me/{applicationId}/cover-letter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApplicationResponse> uploadCoverLetter(
            @PathVariable String applicationId,
            @RequestPart("file") MultipartFile file
    );
}
