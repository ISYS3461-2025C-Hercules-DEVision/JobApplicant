package com.devision.application.controller;

import com.devision.application.api.external.CompanyApplicationApi;
import com.devision.application.api.internal.ApplicationService;
import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class CompanyApiController implements CompanyApplicationApi {

    private final ApplicationService applicationService;

    public CompanyApiController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public ResponseEntity<List<ApplicationSummaryResponse>> listByJobPost(String jobPostId) {
        requireCompany();
        List<ApplicationSummaryView> list = applicationService.listByJobPost(jobPostId);
        return ResponseEntity.ok(list.stream().map(this::toExternal).toList());
    }

    @Override
    public ResponseEntity<List<ApplicationSummaryResponse>> listByCompany(String companyId) {
        requireCompany();
        List<ApplicationSummaryView> list = applicationService.listByCompany(companyId);
        return ResponseEntity.ok(list.stream().map(this::toExternal).toList());
    }

    @Override
    public ResponseEntity<ApplicationResponse> getById(String applicationId) {
        requireCompany();
        ApplicationView view = applicationService.getById(applicationId);
        return ResponseEntity.ok(toExternal(view));
    }

    private void requireCompany() {
        // role name based on our system: "COMPANY", "ADMIN"
        com.devision.application.security.AuthContext.requireAnyRole("COMPANY", "ADMIN");
    }

    private ApplicationResponse toExternal(ApplicationView v) {
        ApplicationResponse r = new ApplicationResponse();
        r.setApplicationId(v.getApplicationId());
        r.setApplicantId(v.getApplicantId());
        r.setJobPostId(v.getJobPostId());
        r.setCompanyId(v.getCompanyId());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());

        if (v.getApplicantCV() != null) r.setApplicantCV(toExternalFile(v.getApplicantCV()));
        if (v.getCoverLetter() != null) r.setCoverLetter(toExternalFile(v.getCoverLetter()));
        return r;
    }

    private ApplicationResponse.FileRefResponse toExternalFile(ApplicationView.FileView f) {
        ApplicationResponse.FileRefResponse fr = new ApplicationResponse.FileRefResponse();
        fr.setFileId(f.getFileId());
        fr.setFileUrl(f.getFileUrl());
        fr.setPublicId(f.getPublicId());   
        fr.setFileType(f.getFileType());
        fr.setCreatedAt(f.getCreatedAt());
        fr.setUpdatedAt(f.getUpdatedAt());
        return fr;
    }

    private ApplicationSummaryResponse toExternal(ApplicationSummaryView v) {
        ApplicationSummaryResponse r = new ApplicationSummaryResponse();
        r.setApplicationId(v.getApplicationId());
        r.setJobPostId(v.getJobPostId());
        r.setCompanyId(v.getCompanyId());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        return r;
    }
}
