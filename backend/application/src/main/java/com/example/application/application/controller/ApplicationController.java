package com.example.application.application.controller;



import com.example.application.application.dto.ApplicationCreateRequest;
import com.example.application.application.dto.ApplicationDTO;
import com.example.application.application.dto.UpdateStatusRequest;
import com.example.application.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/applications")
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
        return applicationService.createApplication(request);
    }

    @PatchMapping("/{applicationId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationDTO updateStatus(@PathVariable String applicationId, @Valid @RequestBody UpdateStatusRequest request){
        return applicationService.updateStatus(applicationId,request.status());
    }

    @DeleteMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveApplication(@PathVariable String applicationId){
        applicationService.archive(applicationId);
    }
}
