package com.hjc.kgms.controller;

import com.hjc.kgms.common.ApiResponse;
import com.hjc.kgms.common.PageResponse;
import com.hjc.kgms.model.dto.ResourceUpdateDTO;
import com.hjc.kgms.model.dto.ResourceUploadDTO;
import com.hjc.kgms.model.dto.SearchFilterDTO;
import com.hjc.kgms.model.vo.HotTagVO;
import com.hjc.kgms.model.vo.ResourceVO;
import com.hjc.kgms.model.vo.UploadBatchResultVO;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.service.ResourceService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/resources/upload")
    public ApiResponse<UploadBatchResultVO> upload(@RequestParam("files") MultipartFile[] files,
                                                   @ModelAttribute ResourceUploadDTO dto,
                                                   @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.upload(files, dto, currentUser));
    }

    @GetMapping("/resources/{id}")
    public ApiResponse<ResourceVO> getById(@PathVariable Long id,
                                           @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.getById(id, currentUser));
    }

    @PutMapping("/resources/{id}")
    public ApiResponse<ResourceVO> update(@PathVariable Long id,
                                          @Validated @RequestBody ResourceUpdateDTO dto,
                                          @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.update(id, dto, currentUser));
    }

    @DeleteMapping("/resources/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal AuthUserDetails currentUser) {
        resourceService.delete(id, currentUser);
        return ApiResponse.ok("删除成功", null);
    }

    @GetMapping("/resources/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id,
                                                        @AuthenticationPrincipal AuthUserDetails currentUser) {
        ResourceVO vo = resourceService.getById(id, currentUser);
        InputStream inputStream = resourceService.downloadStream(id, currentUser);

        HttpHeaders headers = new HttpHeaders();
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(vo.getFileName() == null ? ("resource-" + id) : vo.getFileName(), StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping("/resources/search")
    public ApiResponse<PageResponse<ResourceVO>> search(@RequestBody(required = false) SearchFilterDTO filter,
                                                        @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.search(filter == null ? new SearchFilterDTO() : filter, currentUser));
    }

    @GetMapping("/resources/recent")
    public ApiResponse<List<ResourceVO>> recent(@RequestParam(value = "limit", required = false) Integer limit,
                                                @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.recent(limit, currentUser));
    }

    @GetMapping("/resources/hot-tags")
    public ApiResponse<List<HotTagVO>> hotTags(@RequestParam(value = "limit", required = false) Integer limit,
                                               @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.hotTags(limit, currentUser));
    }

    @PostMapping("/resources/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable("id") Long resourceId,
                                      @AuthenticationPrincipal AuthUserDetails currentUser) {
        resourceService.favorite(resourceId, currentUser);
        return ApiResponse.ok("收藏成功", null);
    }

    @DeleteMapping("/resources/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable("id") Long resourceId,
                                        @AuthenticationPrincipal AuthUserDetails currentUser) {
        resourceService.unfavorite(resourceId, currentUser);
        return ApiResponse.ok("取消收藏成功", null);
    }

    @GetMapping("/users/me/favorites")
    public ApiResponse<PageResponse<ResourceVO>> myFavorites(@RequestParam(value = "pageNo", required = false) Integer pageNo,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                             @AuthenticationPrincipal AuthUserDetails currentUser) {
        return ApiResponse.ok(resourceService.myFavorites(pageNo, pageSize, currentUser));
    }
}
