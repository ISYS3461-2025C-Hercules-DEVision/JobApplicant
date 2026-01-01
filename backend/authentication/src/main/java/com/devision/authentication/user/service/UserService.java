package com.devision.authentication.user.service;

import com.devision.authentication.dto.HandleChangeStatusReqDto;
import com.devision.authentication.dto.LoginRequest;
import com.devision.authentication.dto.RegisterRequest;
import com.devision.authentication.user.entity.User;

import java.util.Map;

public interface UserService {

    User findByEmail(String email);

    User findById(String id);

    // -------- LOCAL REGISTER --------
    User registerLocalUser(RegisterRequest request);

    // -------- LOCAL LOGIN --------
    User loginLocalUser(LoginRequest request);

    User loginLocalAdmin(LoginRequest request);

    // -------- GOOGLE LOGIN/REGISTER --------
    User handleGoogleLogin(Map<String, Object> attributes);

    User updateStatus(HandleChangeStatusReqDto handleChangeStatusReqDto);

    void attachApplicantToUser(String userId, String applicantId);

    void attachAdminToUser(String userId, String adminId);
}

