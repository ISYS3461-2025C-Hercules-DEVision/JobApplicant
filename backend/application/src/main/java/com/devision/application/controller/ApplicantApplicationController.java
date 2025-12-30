package com.devision.application.controller;

import com.devision.application.api.external.ApplicantApplicationApi;
import com.devision.application.api.internal.ApplicationService;
import com.devision.application.dto.external.request.CreateApplicationRequest;
import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.command.UploadCoverLetterCommand;
import com.devision.application.dto.internal.command.UploadCvCommand;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
public class ApplicantApplicationController implements ApplicantApplicationApi {

    private final ApplicationService applicationService;

    public ApplicantApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody CreateApplicationRequest request) {
        String applicantId = currentUserId();

        ApplicationView view = applicationService.create(
                new CreateApplicationCommand(
                        applicantId,
                        request.getJobPostId(),
                        request.getCompanyId()
                )
        );

        return ResponseEntity.ok(toExternal(view));
    }

    @Override
    public ResponseEntity<List<ApplicationSummaryResponse>> myApplications() {
        String applicantId = currentUserId();
        List<ApplicationSummaryView> list = applicationService.listByApplicant(applicantId);
        return ResponseEntity.ok(list.stream().map(this::toExternal).toList());
    }

    @Override
    public ResponseEntity<ApplicationResponse> myApplicationById(String applicationId) {
        String applicantId = currentUserId();
        ApplicationView view = applicationService.getOwnedByApplicant(applicantId, applicationId);
        return ResponseEntity.ok(toExternal(view));
    }

    @Override
    public ResponseEntity<ApplicationResponse> uploadCv(String applicationId, MultipartFile file) {
        String applicantId = currentUserId();
        ApplicationView view = applicationService.uploadCv(new UploadCvCommand(applicantId, applicationId, file));
        return ResponseEntity.ok(toExternal(view));
    }

    @Override
    public ResponseEntity<ApplicationResponse> uploadCoverLetterFile(String applicationId, MultipartFile file) {
        String applicantId = currentUserId();
        ApplicationView view = applicationService.uploadCoverLetterFile(
                new UploadCoverLetterCommand(applicantId, applicationId, file)
        );
        return ResponseEntity.ok(toExternal(view));
    }

    // ---- Helpers ----

    private String currentUserId() {
        // Option A: Gateway forward header
        // return RequestContextHolder.... or SecurityContext
        // For now: read from Spring Security principal or custom header
        // Replace with your actual implementation.
        return com.devision.application.security.AuthContext.requireUserId();
    }

    private ApplicationResponse toExternal(ApplicationView v) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(v.applicationId);
        r.setApplicantId(v.applicantId);
        r.setJobPostId(v.jobPostId);
        r.setCompanyId(v.companyId);
        r.setStatus(v.status);
        r.setCreatedAt(v.createdAt);
        r.setUpdatedAt(v.updatedAt);

        if (v.applicantCV != null) r.setApplicantCV(toExternalFile(v.applicantCV));
        if (v.coverLetter != null) r.setCoverLetter(toExternalFile(v.coverLetter));

        return r;
    }

    private ApplicationResponse.FileRefResponse toExternalFile(ApplicationView.FileView f) {
        ApplicationResponse.FileRefResponse fr = new ApplicationResponse.FileRefResponse();
        fr.setFileId(f.fileId);
        fr.setFileUrl(f.fileUrl);
        fr.setFileType(f.fileType);
        fr.setCreatedAt(f.createdAt);
        return fr;
    }

    private ApplicationSummaryResponse toExternal(ApplicationSummaryView v) {
        ApplicationSummaryResponse r = new ApplicationSummaryResponse();
        r.setId(v.applicationId);
        r.setJobPostId(v.jobPostId);
        r.setCompanyId(v.companyId);
        r.setStatus(v.status);
        r.setCreatedAt(v.createdAt);
        return r;
    }
}
