package com.hjc.kgms.controller;

import com.hjc.kgms.common.ApiResponse;
import com.hjc.kgms.model.dto.CategoryDTO;
import com.hjc.kgms.model.entity.KgCategory;
import com.hjc.kgms.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }
    @GetMapping("/tree") public ApiResponse<List<KgCategory>> tree(@RequestParam(value = "includeDisabled", required = false, defaultValue = "false") boolean includeDisabled) { return ApiResponse.ok(categoryService.tree(includeDisabled)); }
    @PostMapping @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<KgCategory> create(@Validated @RequestBody CategoryDTO dto) { return ApiResponse.ok(categoryService.create(dto)); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<KgCategory> update(@PathVariable Long id, @Validated @RequestBody CategoryDTO dto) { return ApiResponse.ok(categoryService.update(id, dto)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<Void> delete(@PathVariable Long id) { categoryService.delete(id); return ApiResponse.ok("删除成功", null); }
}
