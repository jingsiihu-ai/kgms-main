package com.hjc.kgms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {
    int insert(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
    int delete(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
    Integer exists(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
}
