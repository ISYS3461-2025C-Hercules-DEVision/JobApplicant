package com.devdivision.internal.service;

import com.devdivision.dto.AdminAuthenticationDtos.AdminCreateRequestDTO;
import com.devdivision.dto.AdminAuthenticationDtos.AdminDTO;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminCreateRequestDto;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminDto;

public interface AdminService {
    AdminDTO createSuperAdmin(AdminCreateRequestDTO req);
    ApplicantForAdminDto createApplicantForAdmin(ApplicantForAdminCreateRequestDto req);
}
