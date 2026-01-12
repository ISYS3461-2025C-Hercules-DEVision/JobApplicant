package com.devision.application.controller;

import com.devision.application.dto.ApplicationCreateRequest;
import com.devision.application.dto.ApplicationDTO;
import com.devision.application.dto.CompanyApplicationViewDTO;
import com.devision.application.dto.UpdateStatusRequest;
import com.devision.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/applicant/{applicantId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ApplicationDTO> getApplicationsByApplicantId(@PathVariable String applicantId){
        return applicationService.getApplicationsByApplicantId(applicantId);
    }

    @GetMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationDTO getApplicationById(@PathVariable String applicationId){
        return applicationService.getById(applicationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationDTO createApplication(@Valid @RequestBody ApplicationCreateRequest request){
        log.info("✅ [ApplicationController] HIT createApplication() with request = {}", request);
        return applicationService.createApplication(request);
    }

    @PatchMapping("/{applicationId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationDTO updateStatus(@PathVariable String applicationId, @Valid @RequestBody UpdateStatusRequest request){
        return applicationService.updateStatus(applicationId,request.status());
    }
    @GetMapping("/{companyId}/job-posts/{jobPostId}/applications")
    public List<CompanyApplicationViewDTO> getApplicantsForJobPost(
            @PathVariable String companyId,
            @PathVariable String jobPostId
    ) {
        return applicationService.getApplicationsForJobPost(companyId, jobPostId);
    }

}
