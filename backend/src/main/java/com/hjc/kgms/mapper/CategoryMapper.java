package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.KgCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<KgCategory> selectAll();
    KgCategory selectById(@Param("id") Long id);
    int insert(KgCategory entity);
    int update(KgCategory entity);
    int deleteById(@Param("id") Long id);
    int countChildren(@Param("id") Long id);
    int countResources(@Param("id") Long id);
}
