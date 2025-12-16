package com.devision.applicant.controller;

import com.devision.applicant.model.Applicant;
import com.devision.applicant.service.ApplicantProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/applicants")
public class ApplicantController {
    private final ApplicantProfileService applicantService;

    @Autowired
    ApplicantController(ApplicantProfileService applicantService){
        this.applicantService = applicantService;
    }

    @GetMapping()
    List<Applicant> getApplicants(){
        return applicantService.getAllApplicants();
    }
}
