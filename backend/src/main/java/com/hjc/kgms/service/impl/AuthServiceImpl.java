package com.hjc.kgms.service.impl;

import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.model.dto.LoginRequest;
import com.hjc.kgms.model.vo.CurrentUserVO;
import com.hjc.kgms.model.vo.LoginVO;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.security.JwtTokenService;
import com.hjc.kgms.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }
    @Override
    public LoginVO login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();
            if (!user.isEnabled()) throw new BusinessException(403, "账号已停用");
            LoginVO vo = new LoginVO();
            vo.setToken(jwtTokenService.createToken(user));
            vo.setUser(currentUser(user));
            return vo;
        } catch (BadCredentialsException ex) {
            throw new BusinessException(401, "用户名或密码错误");
        }
    }
    @Override
    public CurrentUserVO currentUser(AuthUserDetails authUserDetails) {
        CurrentUserVO vo = new CurrentUserVO();
        vo.setId(authUserDetails.getId());
        vo.setUsername(authUserDetails.getUsername());
        vo.setName(authUserDetails.getName());
        vo.setRoleId(authUserDetails.getRoleId());
        vo.setRoles(authUserDetails.getRoles());
        return vo;
    }
}
