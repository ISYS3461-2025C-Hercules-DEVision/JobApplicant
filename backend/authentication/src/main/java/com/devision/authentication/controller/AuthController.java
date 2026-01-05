package com.devision.authentication.controller;


import com.devision.authentication.dto.*;
import com.devision.authentication.user.service.AuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthCookieResponse register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        return authService.register(request, response);
    }

    @PostMapping("/login")
    public AuthCookieResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/admin/login")
    public AuthAdminCookieResponse adminLogin(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.adminLogin(request, response);
    }

    @PostMapping("/refresh")
    public AuthCookieResponse refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        return authService.refreshAccessToken(refreshToken, response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, response);
    }
}
