package com.devdivision.internal.service;

import com.devdivision.dto.AdminCreateRequestDTO;
import com.devdivision.dto.AdminDTO;

public interface AdminService {
    AdminDTO createSuperAdmin(AdminCreateRequestDTO req);
}
