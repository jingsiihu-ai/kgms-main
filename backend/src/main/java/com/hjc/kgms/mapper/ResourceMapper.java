package com.hjc.kgms.mapper;

import com.hjc.kgms.model.dto.SearchFilterDTO;
import com.hjc.kgms.model.entity.KgResource;
import com.hjc.kgms.model.vo.ResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourceMapper {
    int insert(KgResource entity);
    int update(KgResource entity);
    KgResource selectById(@Param("id") Long id);
    ResourceVO selectVoById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);
    int deleteById(@Param("id") Long id);
    int deleteResourceTags(@Param("resourceId") Long resourceId);
    int insertResourceTag(@Param("resourceId") Long resourceId, @Param("tagId") Long tagId);
    List<ResourceVO> search(@Param("f") SearchFilterDTO filter, @Param("offset") Integer offset, @Param("limit") Integer limit, @Param("currentUserId") Long currentUserId);
    Long countSearch(@Param("f") SearchFilterDTO filter, @Param("currentUserId") Long currentUserId);
    List<ResourceVO> selectRecent(@Param("limit") Integer limit, @Param("currentUserId") Long currentUserId);
    List<ResourceVO> selectFavorites(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    Long countFavorites(@Param("userId") Long userId);
    int increaseFavoriteCount(@Param("resourceId") Long resourceId);
    int decreaseFavoriteCount(@Param("resourceId") Long resourceId);
    int increaseViewCount(@Param("resourceId") Long resourceId);
}
