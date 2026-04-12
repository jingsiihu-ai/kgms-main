package com.hjc.kgms.security;

import com.hjc.kgms.enums.UserRole;
import com.hjc.kgms.mapper.UserMapper;
import com.hjc.kgms.model.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public DbUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        String roleCode = UserRole.fromRoleId(user.getRoleId()).name();
        return new AuthUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getRoleId(), user.getStatus(), Collections.singletonList(roleCode));
    }
}
