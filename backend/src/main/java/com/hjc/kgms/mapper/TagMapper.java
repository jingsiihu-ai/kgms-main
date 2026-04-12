package com.hjc.kgms.mapper;

import com.hjc.kgms.model.entity.KgTag;
import com.hjc.kgms.model.vo.HotTagVO;
import com.hjc.kgms.model.vo.SimpleOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    List<KgTag> selectAll();
    KgTag selectById(@Param("id") Long id);
    KgTag selectByName(@Param("name") String name);
    int insert(KgTag entity);
    int update(KgTag entity);
    int deleteById(@Param("id") Long id);
    List<SimpleOptionVO> selectOptionsByResourceId(@Param("resourceId") Long resourceId);
    List<HotTagVO> selectHotTags(@Param("limit") Integer limit);
}
