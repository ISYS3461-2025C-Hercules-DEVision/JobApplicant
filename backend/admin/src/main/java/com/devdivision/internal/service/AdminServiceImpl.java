package com.devdivision.internal.service;

import com.devdivision.dto.AdminCreateRequestDTO;
import com.devdivision.dto.AdminDTO;
import com.devdivision.internal.entity.Admin;
import com.devdivision.internal.repo.AdminRepository;
import com.devdivision.mapper.AdminMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public AdminDTO createSuperAdmin(AdminCreateRequestDTO req) {
        if (!adminRepository.existsByAdminEmail(req.adminEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        Admin saved = adminRepository.save(AdminMapper.toEntity(req));
        return AdminMapper.toDTO(saved);
    }
}
