package com.hjc.kgms.controller;

import com.hjc.kgms.common.ApiResponse;
import com.hjc.kgms.model.dto.LoginRequest;
import com.hjc.kgms.model.vo.CurrentUserVO;
import com.hjc.kgms.model.vo.LoginVO;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Validated @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserVO> me(@AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(authService.currentUser(currentUser));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.ok("已退出登录", null);
    }
}
