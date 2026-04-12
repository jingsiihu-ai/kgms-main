package com.hjc.kgms.service.impl;

import com.hjc.kgms.common.PageResponse;
import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.mapper.FavoriteMapper;
import com.hjc.kgms.mapper.ResourceMapper;
import com.hjc.kgms.mapper.TagMapper;
import com.hjc.kgms.model.dto.ResourceUpdateDTO;
import com.hjc.kgms.model.dto.ResourceUploadDTO;
import com.hjc.kgms.model.dto.SearchFilterDTO;
import com.hjc.kgms.model.entity.KgResource;
import com.hjc.kgms.model.vo.HotTagVO;
import com.hjc.kgms.model.vo.ResourceVO;
import com.hjc.kgms.model.vo.UploadBatchResultVO;
import com.hjc.kgms.model.vo.UploadFailedItemVO;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.service.MetadataExtractService;
import com.hjc.kgms.service.ResourceService;
import com.hjc.kgms.storage.StorageManager;
import com.hjc.kgms.storage.StorageResult;
import com.hjc.kgms.util.MetadataExtractResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceMapper resourceMapper;
    private final TagMapper tagMapper;
    private final FavoriteMapper favoriteMapper;
    private final StorageManager storageManager;
    private final MetadataExtractService metadataExtractService;

    public ResourceServiceImpl(ResourceMapper resourceMapper, TagMapper tagMapper, FavoriteMapper favoriteMapper, StorageManager storageManager, MetadataExtractService metadataExtractService) {
        this.resourceMapper = resourceMapper;
        this.tagMapper = tagMapper;
        this.favoriteMapper = favoriteMapper;
        this.storageManager = storageManager;
        this.metadataExtractService = metadataExtractService;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public UploadBatchResultVO upload(MultipartFile[] files, ResourceUploadDTO dto, AuthUserDetails currentUser) {
        if (files == null || files.length == 0) throw new BusinessException("请选择至少一个文件");
        UploadBatchResultVO result = new UploadBatchResultVO();
        result.setTotalCount(files.length);
        List<Long> tagIds = parseIdCsv(dto.getTagIds());
        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                String objectKey = buildObjectKey(currentUser.getId(), originalName);
                StorageResult storageResult = storageManager.upload(objectKey, file.getInputStream(), file.getSize(), file.getContentType());
                MetadataExtractResult metadata = metadataExtractService.extract(file);
                KgResource entity = new KgResource();
                entity.setTitle(resolveTitle(dto.getTitle(), originalName, files.length));
                entity.setDescription(StringUtils.defaultIfBlank(dto.getDescription(), metadata == null ? null : metadata.getSearchableText()));
                entity.setUrl(storageResult.getUrl());
                entity.setUserId(currentUser.getId());
                entity.setStatus(normalizeStatus(dto.getStatus()));
                entity.setViewCount(0); entity.setFavoriteCount(0);
                entity.setResourceFormat(detectFormat(FilenameUtils.getExtension(originalName)));
                entity.setResourceType(detectResourceType(entity.getResourceFormat()));
                entity.setFileName(originalName);
                entity.setFileSize(file.getSize());
                entity.setCategoryId(dto.getCategoryId());
                entity.setApplicableGrade(dto.getApplicableGrade());
                entity.setSemester(dto.getSemester());
                resourceMapper.insert(entity);
                bindTags(entity.getId(), tagIds);
                ResourceVO vo = resourceMapper.selectVoById(entity.getId(), currentUser.getId());
                fillTagsAndFavorite(vo, currentUser.getId());
                result.getSuccessList().add(vo);
            } catch (Exception ex) {
                UploadFailedItemVO failed = new UploadFailedItemVO();
                failed.setOriginalFilename(file.getOriginalFilename());
                failed.setReason(ex.getMessage());
                result.getFailedList().add(failed);
            }
        }
        result.setSuccessCount(result.getSuccessList().size());
        result.setFailCount(result.getFailedList().size());
        return result;
    }

    @Override
    public ResourceVO getById(Long id, AuthUserDetails currentUser) {
        KgResource entity = resourceMapper.selectById(id);
        if (entity == null) throw new BusinessException(404, "资源不存在");
        assertCanAccess(entity, currentUser);
        resourceMapper.increaseViewCount(id);
        ResourceVO vo = resourceMapper.selectVoById(id, currentUser.getId());
        fillTagsAndFavorite(vo, currentUser.getId());
        return vo;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public ResourceVO update(Long id, ResourceUpdateDTO dto, AuthUserDetails currentUser) {
        KgResource existing = resourceMapper.selectById(id);
        if (existing == null) throw new BusinessException(404, "资源不存在");
        assertCanManage(existing, currentUser);
        existing.setTitle(dto.getTitle()); existing.setDescription(dto.getDescription()); existing.setStatus(normalizeStatus(dto.getStatus())); existing.setCategoryId(dto.getCategoryId()); existing.setApplicableGrade(dto.getApplicableGrade()); existing.setSemester(dto.getSemester());
        resourceMapper.update(existing);
        resourceMapper.deleteResourceTags(id); bindTags(id, dto.getTagIds());
        ResourceVO vo = resourceMapper.selectVoById(id, currentUser.getId()); fillTagsAndFavorite(vo, currentUser.getId()); return vo;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, AuthUserDetails currentUser) {
        KgResource existing = resourceMapper.selectById(id); if (existing == null) return;
        assertCanManage(existing, currentUser);
        resourceMapper.deleteResourceTags(id); resourceMapper.deleteById(id);
        try { storageManager.delete("LOCAL", extractObjectKey(existing.getUrl())); } catch (Exception ignored) {}
    }

    @Override
    public PageResponse<ResourceVO> search(SearchFilterDTO filter, AuthUserDetails currentUser) {
        SearchFilterDTO f = normalizeSearch(filter);
        int offset = (f.getPageNo() - 1) * f.getPageSize();
        List<ResourceVO> records = resourceMapper.search(f, offset, f.getPageSize(), currentUser.getId());
        Long total = resourceMapper.countSearch(f, currentUser.getId());
        records.forEach(item -> fillTagsAndFavorite(item, currentUser.getId()));
        return new PageResponse<>(total == null ? 0L : total, f.getPageNo(), f.getPageSize(), records);
    }

    @Override
    public List<ResourceVO> recent(Integer limit, AuthUserDetails currentUser) {
        int safeLimit = limit == null || limit <= 0 ? 8 : Math.min(limit, 30);
        List<ResourceVO> records = resourceMapper.selectRecent(safeLimit, currentUser.getId());
        records.forEach(item -> fillTagsAndFavorite(item, currentUser.getId()));
        return records;
    }

    @Override public List<HotTagVO> hotTags(Integer limit, AuthUserDetails currentUser) { return tagMapper.selectHotTags(limit == null || limit <= 0 ? 10 : Math.min(limit, 20)); }

    @Override @Transactional(rollbackFor = Exception.class)
    public void favorite(Long resourceId, AuthUserDetails currentUser) {
        KgResource resource = resourceMapper.selectById(resourceId); if (resource == null) throw new BusinessException(404, "资源不存在"); assertCanAccess(resource, currentUser);
        if (favoriteMapper.exists(currentUser.getId(), resourceId) == 0) { favoriteMapper.insert(currentUser.getId(), resourceId); resourceMapper.increaseFavoriteCount(resourceId); }
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public void unfavorite(Long resourceId, AuthUserDetails currentUser) {
        if (favoriteMapper.exists(currentUser.getId(), resourceId) > 0) { favoriteMapper.delete(currentUser.getId(), resourceId); resourceMapper.decreaseFavoriteCount(resourceId); }
    }

    @Override
    public PageResponse<ResourceVO> myFavorites(Integer pageNo, Integer pageSize, AuthUserDetails currentUser) {
        int pno = pageNo == null || pageNo <= 0 ? 1 : pageNo; int psz = pageSize == null || pageSize <= 0 ? 10 : Math.min(pageSize, 50); int offset = (pno - 1) * psz;
        List<ResourceVO> records = resourceMapper.selectFavorites(currentUser.getId(), offset, psz); Long total = resourceMapper.countFavorites(currentUser.getId()); records.forEach(item -> fillTagsAndFavorite(item, currentUser.getId()));
        return new PageResponse<>(total == null ? 0L : total, pno, psz, records);
    }

    @Override
    public InputStream downloadStream(Long resourceId, AuthUserDetails currentUser) {
        KgResource entity = resourceMapper.selectById(resourceId); if (entity == null) throw new BusinessException(404, "资源不存在"); assertCanAccess(entity, currentUser);
        return storageManager.download("LOCAL", extractObjectKey(entity.getUrl()));
    }

    private void fillTagsAndFavorite(ResourceVO vo, Long userId) { if (vo != null) { vo.setTags(tagMapper.selectOptionsByResourceId(vo.getId())); vo.setFavorite(favoriteMapper.exists(userId, vo.getId()) > 0); } }
    private void bindTags(Long resourceId, List<Long> tagIds) { if (tagIds != null) for (Long tagId : tagIds.stream().filter(it -> it != null).distinct().collect(Collectors.toList())) resourceMapper.insertResourceTag(resourceId, tagId); }
    private List<Long> parseIdCsv(String csv) { if (StringUtils.isBlank(csv)) return Collections.emptyList(); return Arrays.stream(csv.split(",")).map(String::trim).filter(StringUtils::isNotBlank).map(Long::valueOf).distinct().collect(Collectors.toList()); }
    private SearchFilterDTO normalizeSearch(SearchFilterDTO filter) { SearchFilterDTO f = filter == null ? new SearchFilterDTO() : filter; if (f.getPageNo() == null || f.getPageNo() <= 0) f.setPageNo(1); if (f.getPageSize() == null || f.getPageSize() <= 0) f.setPageSize(12); if (f.getPageSize() > 50) f.setPageSize(50); if (StringUtils.isBlank(f.getSortBy())) f.setSortBy("created_at"); if (StringUtils.isBlank(f.getSortOrder())) f.setSortOrder("desc"); return f; }
    private void assertCanAccess(KgResource resource, AuthUserDetails user) { if (resource.getUserId().equals(user.getId()) || "public".equalsIgnoreCase(resource.getStatus())) return; throw new BusinessException(403, "无权访问该资源"); }
    private void assertCanManage(KgResource resource, AuthUserDetails user) { if (resource.getUserId().equals(user.getId()) || user.getRoles().contains("PRINCIPAL")) return; throw new BusinessException(403, "无权操作该资源"); }
    private String resolveTitle(String title, String originalName, int batchSize) { return StringUtils.isNotBlank(title) && batchSize == 1 ? title : FilenameUtils.getBaseName(originalName); }
    private String detectFormat(String ext) { String e = StringUtils.defaultString(ext).toLowerCase(Locale.ROOT); if (Arrays.asList("jpg","jpeg","png","gif","bmp","webp").contains(e)) return "img"; if (Arrays.asList("mp4","avi","mov","wmv","mkv").contains(e)) return "vid"; if ("pdf".equals(e)) return "pdf"; if (Arrays.asList("ppt","pptx").contains(e)) return "ppt"; return "doc"; }
    private int detectResourceType(String format) { if ("img".equals(format)) return 0; if ("vid".equals(format)) return 1; return 2; }
    private String normalizeStatus(String status) { return "private".equalsIgnoreCase(status) ? "private" : "public"; }
    private String buildObjectKey(Long userId, String originalName) { String base = FilenameUtils.getBaseName(StringUtils.defaultIfBlank(originalName, "file")).replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5-_]", "_"); String ext = FilenameUtils.getExtension(originalName); return "resources/" + userId + "/" + System.currentTimeMillis() + "-" + base + (StringUtils.isBlank(ext) ? "" : "." + ext); }
    private String extractObjectKey(String url) { if (url == null) throw new BusinessException(404, "文件地址不存在"); int idx = url.indexOf("/storage/"); return idx >= 0 ? url.substring(idx + 9) : (url.startsWith("/") ? url.substring(1) : url); }
}
