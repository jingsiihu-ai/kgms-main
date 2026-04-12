package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<SysRole> selectAll();
}
