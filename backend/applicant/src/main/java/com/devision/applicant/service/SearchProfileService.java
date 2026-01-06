package com.devision.applicant.service;

import com.devision.applicant.dto.SearchProfileCreateRequest;
import com.devision.applicant.dto.SearchProfileDTO;
import com.devision.applicant.dto.SearchProfileUpdateRequest;

import java.util.List;

public interface SearchProfileService {
    List<SearchProfileDTO> getByApplicantId(String applicantId);

    SearchProfileDTO create(SearchProfileCreateRequest request);

    SearchProfileDTO update(String id, SearchProfileUpdateRequest request);

    void delete(String id);
}
