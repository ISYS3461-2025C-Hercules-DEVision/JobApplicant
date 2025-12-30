package com.devision.application.controller;

import com.devision.application.api.external.CompanyApplicationApi;
import com.devision.application.api.internal.ApplicationService;
import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


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
