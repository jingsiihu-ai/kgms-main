package com.hjc.kgms.util;

import com.hjc.kgms.security.AuthUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static AuthUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserDetails)) {
            return null;
        }
        return (AuthUserDetails) authentication.getPrincipal();
    }

    public static Long currentUserId() {
        AuthUserDetails user = currentUser();
        return user == null ? null : user.getId();
    }
}
