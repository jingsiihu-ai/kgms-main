package com.hjc.kgms.controller;

import com.hjc.kgms.common.ApiResponse;
import com.hjc.kgms.model.dto.TagDTO;
import com.hjc.kgms.model.entity.KgTag;
import com.hjc.kgms.service.DictionaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DictionaryController {
    private final DictionaryService dictionaryService;
    public DictionaryController(DictionaryService dictionaryService) { this.dictionaryService = dictionaryService; }
    @GetMapping("/classes") public ApiResponse<List<Object>> listClasses() { return ApiResponse.ok(Collections.emptyList()); }
    @GetMapping("/themes") public ApiResponse<List<Object>> listThemes() { return ApiResponse.ok(Collections.emptyList()); }
    @GetMapping("/tags") public ApiResponse<List<KgTag>> listTags() { return ApiResponse.ok(dictionaryService.listTags()); }
    @PostMapping("/tags") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<KgTag> createTag(@Validated @RequestBody TagDTO dto) { return ApiResponse.ok(dictionaryService.createTag(dto)); }
    @PutMapping("/tags/{id}") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<KgTag> updateTag(@PathVariable Long id, @Validated @RequestBody TagDTO dto) { return ApiResponse.ok(dictionaryService.updateTag(id, dto)); }
    @DeleteMapping("/tags/{id}") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<Void> deleteTag(@PathVariable Long id) { dictionaryService.deleteTag(id); return ApiResponse.ok("删除成功", null); }
}
