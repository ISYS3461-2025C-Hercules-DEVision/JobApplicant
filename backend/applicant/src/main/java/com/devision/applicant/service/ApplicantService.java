package com.devision.applicant.service;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.ApplicantUpdateRequest;

import java.util.List;

public interface ApplicantService {
    ApplicantDTO create(ApplicantCreateRequest request);

    ApplicantDTO getById(String id);

    List<ApplicantDTO> getAll();

    ApplicantDTO update(String id, ApplicantUpdateRequest request);

    void delete(String id);
}
