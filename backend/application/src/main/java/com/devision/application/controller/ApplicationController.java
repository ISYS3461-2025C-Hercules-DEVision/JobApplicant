package com.devision.application.controller;

import com.devision.application.api.internal.ApplicationService;
import com.devision.application.dto.external.request.CreateApplicationRequest;
import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/api/admin/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Admin get all applications
    @GetMapping
    public ResponseEntity<List<ApplicationSummaryResponse>> listAll() {
        requireAdmin();

        List<ApplicationSummaryView> list = applicationService.listAll();
        return ResponseEntity.ok(list.stream().map(this::toExternal).toList());
    }

    // Admin gets any application by id
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable("id") String id) {
        requireAdmin();
        ApplicationView view = applicationService.getById(id);
        return ResponseEntity.ok(toExternal(view));
    }

    // Admin can list by applicant id
    @GetMapping("/by-applicant/{applicantId}")
    public ResponseEntity<List<ApplicationSummaryResponse>> listByApplicant(@PathVariable String applicantId) {
        requireAdmin();
        List<ApplicationSummaryView> list = applicationService.listByApplicant(applicantId);
        return ResponseEntity.ok(list.stream().map(this::toExternal).toList());
    }

    // admin can create application on behalf? (usually no)
    @PostMapping
    public ResponseEntity<ApplicationResponse> createForApplicant(
            @RequestParam String applicantId,
            @Valid @RequestBody CreateApplicationRequest request
    ) {
        requireAdmin();
        ApplicationView view = applicationService.create(
                new CreateApplicationCommand(applicantId, request.getJobPostId(), request.getCompanyId())
        );
        return ResponseEntity.ok(toExternal(view));
    }

    private void requireAdmin() {
        com.devision.application.security.AuthContext.requireRole("ADMIN");
    }

    private ApplicationResponse toExternal(ApplicationView v) {
        ApplicationResponse r = new ApplicationResponse();
        r.setApplicationId(v.applicationId);
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
        fr.setFileId(f.getFileId);
        fr.setFileUrl(f.getFileUrl);
        fr.setPublicId(f.getPublicId);
        fr.setFileType(f.getFileType);
        fr.setCreatedAt(f.getCreatedAt);
        fr.setUpdatedAt(f.getUpdatedAt);
        return fr;
    }

    private ApplicationSummaryResponse toExternal(ApplicationSummaryView v) {
        ApplicationSummaryResponse r = new ApplicationSummaryResponse();
        r.setApplicationId(v.applicationId);
        r.setJobPostId(v.jobPostId);
        r.setCompanyId(v.companyId);
        r.setStatus(v.status);
        r.setCreatedAt(v.createdAt);
        return r;
    }
}
