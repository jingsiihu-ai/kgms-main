package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.SysUser;
import com.hjc.kgms.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    SysUser selectByUsername(@Param("username") String username);
    SysUser selectById(@Param("id") Long id);
    List<UserVO> selectUsers(@Param("keyword") String keyword);
    Integer countByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);
    Integer countByPhone(@Param("phone") String phone, @Param("excludeId") Long excludeId);
    int insertUser(SysUser user);
    int updateUser(SysUser user);
}
