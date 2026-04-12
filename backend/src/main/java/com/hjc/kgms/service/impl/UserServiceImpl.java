package com.hjc.kgms.service.impl;

import com.hjc.kgms.enums.UserRole;
import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.mapper.UserMapper;
import com.hjc.kgms.model.dto.UserCreateDTO;
import com.hjc.kgms.model.dto.UserUpdateDTO;
import com.hjc.kgms.model.entity.SysRole;
import com.hjc.kgms.model.entity.SysUser;
import com.hjc.kgms.model.vo.UserVO;
import com.hjc.kgms.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) { this.userMapper = userMapper; this.passwordEncoder = passwordEncoder; }
    @Override
    public List<UserVO> listUsers(String keyword) {
        List<UserVO> users = userMapper.selectUsers(keyword);
        users.forEach(u -> u.setRoles(java.util.Collections.singletonList(UserRole.fromRoleId(u.getRoleId()).name())));
        return users;
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateDTO dto) {
        validateUnique(dto.getUsername(), dto.getPhone(), null);
        SysUser user = new SysUser(); user.setUsername(dto.getUsername()); user.setPassword(passwordEncoder.encode(dto.getPassword())); user.setPhone(dto.getPhone()); user.setRoleId(dto.getRoleId()); user.setName(dto.getName()); user.setStatus(dto.getStatus());
        userMapper.insertUser(user);
        return listUsers(dto.getUsername()).stream().filter(u -> u.getId().equals(user.getId())).findFirst().orElseThrow(() -> new BusinessException("创建用户失败"));
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long userId, UserUpdateDTO dto) {
        SysUser existing = userMapper.selectById(userId); if (existing == null) throw new BusinessException(404, "用户不存在");
        validateUnique(existing.getUsername(), dto.getPhone(), userId);
        existing.setPhone(dto.getPhone()); existing.setRoleId(dto.getRoleId()); existing.setName(dto.getName()); existing.setStatus(dto.getStatus());
        userMapper.updateUser(existing);
        return listUsers(existing.getUsername()).stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() -> new BusinessException("更新用户失败"));
    }
    @Override
    public List<SysRole> listRoles() {
        return Arrays.stream(UserRole.values()).map(role -> new SysRole((long) role.getRoleId(), role.name(), role.getDisplayName())).collect(Collectors.toList());
    }
    private void validateUnique(String username, String phone, Long excludeId) {
        if (userMapper.countByUsername(username, excludeId) > 0) throw new BusinessException("用户名已存在");
        if (userMapper.countByPhone(phone, excludeId) > 0) throw new BusinessException("手机号已存在");
    }
}
