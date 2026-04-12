package com.hjc.kgms.service.impl;

import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.mapper.TagMapper;
import com.hjc.kgms.model.dto.TagDTO;
import com.hjc.kgms.model.entity.KgTag;
import com.hjc.kgms.service.DictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    private final TagMapper tagMapper;
    public DictionaryServiceImpl(TagMapper tagMapper) { this.tagMapper = tagMapper; }
    @Override public List<KgTag> listTags() { return tagMapper.selectAll(); }
    @Override @Transactional(rollbackFor = Exception.class)
    public KgTag createTag(TagDTO dto) {
        if (tagMapper.selectByName(dto.getName()) != null) throw new BusinessException("标签已存在");
        KgTag tag = new KgTag(); tag.setName(dto.getName()); tagMapper.insert(tag); return tagMapper.selectById(tag.getId());
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public KgTag updateTag(Long id, TagDTO dto) {
        KgTag existing = tagMapper.selectById(id); if (existing == null) throw new BusinessException(404, "标签不存在");
        KgTag byName = tagMapper.selectByName(dto.getName()); if (byName != null && !byName.getId().equals(id)) throw new BusinessException("标签已存在");
        existing.setName(dto.getName()); tagMapper.update(existing); return tagMapper.selectById(id);
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) { tagMapper.deleteById(id); }
}
