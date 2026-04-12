package com.hjc.kgms.service;

import com.hjc.kgms.common.PageResponse;
import com.hjc.kgms.model.dto.ResourceUpdateDTO;
import com.hjc.kgms.model.dto.ResourceUploadDTO;
import com.hjc.kgms.model.dto.SearchFilterDTO;
import com.hjc.kgms.model.vo.HotTagVO;
import com.hjc.kgms.model.vo.ResourceVO;
import com.hjc.kgms.model.vo.UploadBatchResultVO;
import com.hjc.kgms.security.AuthUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ResourceService {
    UploadBatchResultVO upload(MultipartFile[] files, ResourceUploadDTO dto, AuthUserDetails currentUser);

    ResourceVO getById(Long id, AuthUserDetails currentUser);

    ResourceVO update(Long id, ResourceUpdateDTO dto, AuthUserDetails currentUser);

    void delete(Long id, AuthUserDetails currentUser);

    PageResponse<ResourceVO> search(SearchFilterDTO filter, AuthUserDetails currentUser);

    List<ResourceVO> recent(Integer limit, AuthUserDetails currentUser);

    List<HotTagVO> hotTags(Integer limit, AuthUserDetails currentUser);

    void favorite(Long resourceId, AuthUserDetails currentUser);

    void unfavorite(Long resourceId, AuthUserDetails currentUser);

    PageResponse<ResourceVO> myFavorites(Integer pageNo, Integer pageSize, AuthUserDetails currentUser);

    InputStream downloadStream(Long resourceId, AuthUserDetails currentUser);
}
