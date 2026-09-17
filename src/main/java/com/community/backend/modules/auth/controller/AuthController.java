package com.community.backend.modules.auth.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.modules.auth.dto.LoginRequest;
import com.community.backend.modules.auth.dto.RefreshTokenRequest;
import com.community.backend.modules.auth.dto.RegisterRequest;
import com.community.backend.modules.auth.dto.TokenResponse;
import com.community.backend.modules.auth.service.AuthService;
import com.community.backend.modules.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ApiResponse.success(null);
    }
}
