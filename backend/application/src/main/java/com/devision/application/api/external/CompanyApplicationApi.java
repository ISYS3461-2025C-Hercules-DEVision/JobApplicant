package com.devision.application.api.external;

import com.devision.application.dto.external.response.ApplicationResponse;
import com.devision.application.dto.external.response.ApplicationSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Company Applications (Job Manager)")
@RequestMapping("/api/v1/company/applications")
public interface CompanyApplicationApi {

    /**
     * List applications by jobPostId (partner/internal).
     * Gateway should enforce role = JOB_MANAGER / ADMIN / COMPANY.
     * Application-service can also double-check role if you want defense-in-depth.
     */
    @GetMapping("/by-job/{jobPostId}")
    ResponseEntity<List<ApplicationSummaryResponse>> listByJobPost(@PathVariable String jobPostId);

    /**
     * List applications by companyId (partner/internal).
     */
    @GetMapping("/by-company/{companyId}")
    ResponseEntity<List<ApplicationSummaryResponse>> listByCompany(@PathVariable String companyId);

    /**
     * Get full application detail by applicationId (partner/internal).
     */
    @GetMapping("/{applicationId}")
    ResponseEntity<ApplicationResponse> getById(@PathVariable String applicationId);
}
