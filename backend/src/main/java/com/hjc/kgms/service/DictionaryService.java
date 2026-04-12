package com.hjc.kgms.service;

import com.hjc.kgms.model.dto.TagDTO;
import com.hjc.kgms.model.entity.KgTag;

import java.util.List;

public interface DictionaryService {
    List<KgTag> listTags();
    KgTag createTag(TagDTO dto);
    KgTag updateTag(Long id, TagDTO dto);
    void deleteTag(Long id);
}
