package com.devdivision.internal.service;

import com.devdivision.dto.AdminAuthenticationDtos.AdminCreateRequestDTO;
import com.devdivision.dto.AdminAuthenticationDtos.AdminDTO;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminCreateRequestDto;
import com.devdivision.dto.ApplicantForAdminAuthenticationDtos.ApplicantForAdminDto;
import com.devdivision.internal.entity.Admin;
import com.devdivision.internal.entity.ApplicantForAdmin;
import com.devdivision.internal.repo.AdminRepository;
import com.devdivision.internal.repo.ApplicantForAdminRepository;
import com.devdivision.mapper.AdminMapper;
import com.devdivision.mapper.ApplicantForAdminMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final ApplicantForAdminRepository applicantForAdminRepository;

    public AdminServiceImpl(AdminRepository adminRepository, ApplicantForAdminRepository applicantForAdminRepository) {
        this.adminRepository = adminRepository;
        this.applicantForAdminRepository = applicantForAdminRepository;

    }

    @Override
    public AdminDTO createSuperAdmin(AdminCreateRequestDTO req) {
        if (adminRepository.existsByAdminEmail(req.adminEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        Admin saved = adminRepository.save(AdminMapper.toEntity(req));
        return AdminMapper.toDTO(saved);
    }

    @Override
    public ApplicantForAdminDto createApplicantForAdmin(ApplicantForAdminCreateRequestDto req) {
        if(applicantForAdminRepository.findByEmail(req.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        ApplicantForAdmin admin = applicantForAdminRepository.save(ApplicantForAdminMapper.toEntity(req));
        return ApplicantForAdminMapper.toDTO(admin);
    }
}
