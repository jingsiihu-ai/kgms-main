package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.KgTheme;
import com.hjc.kgms.model.vo.SimpleOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThemeMapper {
    List<KgTheme> selectAll();

    KgTheme selectById(@Param("id") Long id);

    int insert(KgTheme entity);

    int update(KgTheme entity);

    int deleteById(@Param("id") Long id);

    List<SimpleOptionVO> selectOptionsByResourceId(@Param("resourceId") Long resourceId);
}
