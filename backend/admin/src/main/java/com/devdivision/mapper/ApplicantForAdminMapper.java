package com.devdivision.mapper;

import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminCreateRequestDto;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminDto;
import com.devdivision.internal.entity.ApplicantForAdmin;

public class ApplicantForAdminMapper {
    private ApplicantForAdminMapper (){};

    public  static ApplicantForAdmin toEntity(ApplicantForAdminCreateRequestDto req ) {
        ApplicantForAdmin applicantForAdmin = new ApplicantForAdmin();
        applicantForAdmin.setEmail(req.email());
        applicantForAdmin.setFullName(req.fullName());
        applicantForAdmin.setPhoneNumber(req.phoneNumber());
        applicantForAdmin.setCountry(req.country());
        return applicantForAdmin;
    }
    public static ApplicantForAdminDto toDTO(ApplicantForAdmin applicantForAdmin) {
        return new ApplicantForAdminDto(
                applicantForAdmin.getId(),
                applicantForAdmin.getEmail(),
                applicantForAdmin.getFullName(),
                applicantForAdmin.getPhoneNumber(),
                applicantForAdmin.getCountry(),
                applicantForAdmin.getIsActivated()
        );
    }

}
