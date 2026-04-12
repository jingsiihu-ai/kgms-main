package com.hjc.kgms.service;

import com.hjc.kgms.model.dto.UserCreateDTO;
import com.hjc.kgms.model.dto.UserUpdateDTO;
import com.hjc.kgms.model.entity.SysRole;
import com.hjc.kgms.model.vo.UserVO;

import java.util.List;

public interface UserService {
    List<UserVO> listUsers(String keyword);
    UserVO createUser(UserCreateDTO dto);
    UserVO updateUser(Long userId, UserUpdateDTO dto);
    List<SysRole> listRoles();
}
