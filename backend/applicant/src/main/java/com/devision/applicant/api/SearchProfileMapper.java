package com.devision.applicant.api;

import com.devision.applicant.dto.SearchProfileCreateRequest;
import com.devision.applicant.dto.SearchProfileDTO;
import com.devision.applicant.dto.SearchProfileUpdateRequest;
import com.devision.applicant.model.SearchProfile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SearchProfileMapper {

    private SearchProfileMapper() {
    }

    public static List<SearchProfileDTO> toDtoList(List<SearchProfile> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<SearchProfileDTO> dtos = new ArrayList<>(entities.size());
        for (SearchProfile entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    public static SearchProfile toEntity(SearchProfileCreateRequest request) {
        SearchProfile searchProfile = new SearchProfile();
        searchProfile.setProfileName(request.profileName());
        searchProfile.setDesiredCountry(request.desiredCountry());
        searchProfile.setDesiredCity(request.desiredCity());
        searchProfile.setDesiredMinSalary(request.desiredMinSalary());
        searchProfile.setDesiredMaxSalary(request.desiredMaxSalary());
        searchProfile.setJobTitles(request.jobTitles());
        searchProfile.setTechnicalBackground(request.technicalBackground());
        searchProfile.setEmploymentStatus(request.employmentStatus());
        searchProfile.setIsActive(true);
        return searchProfile;
    }

    public static void updateEntity(SearchProfile searchProfile, SearchProfileUpdateRequest request) {
        if(request.profileName() != null) searchProfile.setProfileName(request.profileName());
        if(request.desiredCountry() != null) searchProfile.setDesiredCountry(request.desiredCountry());
        if(request.desiredCity() != null) searchProfile.setDesiredCity(request.desiredCity());
        if(request.desiredMinSalary() != null) searchProfile.setDesiredMinSalary(request.desiredMinSalary());
        if(request.desiredMaxSalary() != null) searchProfile.setDesiredMaxSalary(request.desiredMaxSalary());
        if(request.jobTitles() != null) searchProfile.setJobTitles(request.jobTitles());
        if(request.technicalBackground() != null) searchProfile.setTechnicalBackground(request.technicalBackground());
        if(request.employmentStatus() != null) searchProfile.setEmploymentStatus(request.employmentStatus());
        searchProfile.setUpdatedAt(Instant.now());
    }

    public static SearchProfileDTO toDTO(SearchProfile s){
        return new SearchProfileDTO(
                s.getSearchProfileId(),
                s.getApplicantId(),
                s.getProfileName(),
                s.getDesiredCountry(),
                s.getDesiredCity(),
                s.getDesiredMinSalary(),
                s.getDesiredMaxSalary(),
                s.getJobTitles(),
                s.getTechnicalBackground(),
                s.getEmploymentStatus(),
                Boolean.TRUE.equals(s.getIsActive()),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

}
