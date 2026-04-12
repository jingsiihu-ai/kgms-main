package com.hjc.kgms.service;

import com.hjc.kgms.model.dto.LoginRequest;
import com.hjc.kgms.model.vo.CurrentUserVO;
import com.hjc.kgms.model.vo.LoginVO;
import com.hjc.kgms.security.AuthUserDetails;

public interface AuthService {
    LoginVO login(LoginRequest request);
    CurrentUserVO currentUser(AuthUserDetails authUserDetails);
}
