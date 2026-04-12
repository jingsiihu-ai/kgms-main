package com.hjc.kgms.service.impl;

import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.mapper.CategoryMapper;
import com.hjc.kgms.model.dto.CategoryDTO;
import com.hjc.kgms.model.entity.KgCategory;
import com.hjc.kgms.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    public CategoryServiceImpl(CategoryMapper categoryMapper) { this.categoryMapper = categoryMapper; }
    @Override
    public List<KgCategory> tree(boolean includeDisabled) {
        List<KgCategory> all = categoryMapper.selectAll();
        if (!includeDisabled) all = all.stream().filter(it -> it.getEnabled() == null || it.getEnabled() == 1).collect(Collectors.toList());
        Map<Long, KgCategory> map = new LinkedHashMap<>();
        for (KgCategory item : all) { item.setChildren(new ArrayList<>()); map.put(item.getId(), item); }
        List<KgCategory> roots = new ArrayList<>();
        for (KgCategory item : all) {
            if (item.getParentId() == null) roots.add(item); else { KgCategory parent = map.get(item.getParentId()); if (parent != null) parent.getChildren().add(item); }
        }
        sortTree(roots); return roots;
    }
    private void sortTree(List<KgCategory> list) {
        list.sort(Comparator.comparing(KgCategory::getSortOrder, Comparator.nullsLast(Integer::compareTo)).thenComparing(KgCategory::getId));
        for (KgCategory item : list) sortTree(item.getChildren());
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public KgCategory create(CategoryDTO dto) {
        validate(dto, null); KgCategory entity = new KgCategory();
        entity.setParentId(dto.getParentId()); entity.setName(dto.getName()); entity.setLevel(dto.getLevel()); entity.setSortOrder(dto.getSortOrder()); entity.setEnabled(dto.getEnabled());
        categoryMapper.insert(entity); return categoryMapper.selectById(entity.getId());
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public KgCategory update(Long id, CategoryDTO dto) {
        KgCategory entity = categoryMapper.selectById(id); if (entity == null) throw new BusinessException(404, "目录不存在");
        validate(dto, id); entity.setParentId(dto.getParentId()); entity.setName(dto.getName()); entity.setLevel(dto.getLevel()); entity.setSortOrder(dto.getSortOrder()); entity.setEnabled(dto.getEnabled());
        categoryMapper.update(entity); return categoryMapper.selectById(id);
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (categoryMapper.countChildren(id) > 0) throw new BusinessException("请先删除子目录");
        if (categoryMapper.countResources(id) > 0) throw new BusinessException("当前目录下仍有关联资源，无法删除");
        categoryMapper.deleteById(id);
    }
    private void validate(CategoryDTO dto, Long selfId) {
        if (dto.getLevel() < 1 || dto.getLevel() > 3) throw new BusinessException("目录层级只能为1-3");
        if (dto.getLevel() == 1 && dto.getParentId() != null) throw new BusinessException("一级目录不能设置父级");
        if (dto.getLevel() > 1 && dto.getParentId() == null) throw new BusinessException("二级、三级目录必须有父级");
        if (dto.getParentId() != null) {
            if (selfId != null && selfId.equals(dto.getParentId())) throw new BusinessException("父级目录不能是自己");
            KgCategory parent = categoryMapper.selectById(dto.getParentId()); if (parent == null) throw new BusinessException("父级目录不存在");
            if (parent.getLevel() + 1 != dto.getLevel()) throw new BusinessException("目录层级与父级不匹配");
        }
    }
}
