package com.hjc.kgms.service;

import com.hjc.kgms.model.dto.CategoryDTO;
import com.hjc.kgms.model.entity.KgCategory;

import java.util.List;

public interface CategoryService {
    List<KgCategory> tree(boolean includeDisabled);
    KgCategory create(CategoryDTO dto);
    KgCategory update(Long id, CategoryDTO dto);
    void delete(Long id);
}
