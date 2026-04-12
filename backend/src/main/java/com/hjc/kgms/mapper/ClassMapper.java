package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.KgClass;
import com.hjc.kgms.model.vo.SimpleOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassMapper {
    List<KgClass> selectAll();

    KgClass selectById(@Param("id") Long id);

    int insert(KgClass entity);

    int update(KgClass entity);

    int deleteById(@Param("id") Long id);

    List<SimpleOptionVO> selectOptionsByResourceId(@Param("resourceId") Long resourceId);
}
