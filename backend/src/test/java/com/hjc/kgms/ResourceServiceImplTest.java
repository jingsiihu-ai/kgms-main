package com.hjc.kgms;

import com.hjc.kgms.common.PageResponse;
import com.hjc.kgms.enums.ResourceType;
import com.hjc.kgms.exception.BusinessException;
import com.hjc.kgms.mapper.ClassMapper;
import com.hjc.kgms.mapper.FavoriteMapper;
import com.hjc.kgms.mapper.ResourceMapper;
import com.hjc.kgms.mapper.TagMapper;
import com.hjc.kgms.mapper.ThemeMapper;
import com.hjc.kgms.model.dto.SearchFilterDTO;
import com.hjc.kgms.model.dto.ResourceUploadDTO;
import com.hjc.kgms.model.entity.KgClass;
import com.hjc.kgms.model.entity.KgResource;
import com.hjc.kgms.model.entity.KgTheme;
import com.hjc.kgms.model.vo.ResourceVO;
import com.hjc.kgms.model.vo.SimpleOptionVO;
import com.hjc.kgms.model.vo.UploadBatchResultVO;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.service.MetadataExtractService;
import com.hjc.kgms.service.impl.ResourceServiceImpl;
import com.hjc.kgms.storage.StorageManager;
import com.hjc.kgms.storage.StorageResult;
import com.hjc.kgms.util.FilenameMetaParser;
import com.hjc.kgms.util.MetadataExtractResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ClassMapper classMapper;
    @Mock
    private ThemeMapper themeMapper;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private FavoriteMapper favoriteMapper;
    @Mock
    private MetadataExtractService metadataExtractService;
    @Mock
    private StorageManager storageManager;
    @Spy
    private FilenameMetaParser filenameMetaParser = new FilenameMetaParser();

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void shouldNormalizeSearchFilterBeforeQuery() {
        SearchFilterDTO filter = new SearchFilterDTO();
        filter.setPageNo(0);
        filter.setPageSize(1000);
        filter.setSortBy("drop_table");
        filter.setSortOrder(null);

        AuthUserDetails user = new AuthUserDetails(2L, "teacher_a", "", "李老师", 1L, 1, Collections.singletonList("TEACHER"));

        when(resourceMapper.search(any(), anyInt(), anyInt(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(resourceMapper.countSearch(any(), anyBoolean(), anyLong(), anyLong())).thenReturn(0L);

        PageResponse<ResourceVO> page = resourceService.search(filter, user);
        Assertions.assertEquals(1, page.getPageNo());
        Assertions.assertEquals(50, page.getPageSize());

        ArgumentCaptor<SearchFilterDTO> captor = ArgumentCaptor.forClass(SearchFilterDTO.class);
        org.mockito.Mockito.verify(resourceMapper).search(captor.capture(), eq(0), eq(50), eq(false), eq(2L), eq(1L));
        Assertions.assertEquals("created_at", captor.getValue().getSortBy());
    }

    @Test
    void shouldDenyAccessToPrivateResourceFromOtherUser() {
        KgResource resource = new KgResource();
        resource.setId(10L);
        resource.setUploaderId(2L);
        resource.setVisibility("PRIVATE");

        when(resourceMapper.selectById(10L)).thenReturn(resource);

        AuthUserDetails user = new AuthUserDetails(3L, "teacher_b", "", "陈老师", 2L, 1, Collections.singletonList("TEACHER"));

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> resourceService.getById(10L, user));
        Assertions.assertEquals(403, ex.getCode());
    }

    @Test
    void shouldPreferManualFieldsWhenFilenameInvalid() {
        KgClass clazz = new KgClass();
        clazz.setId(1L);
        clazz.setClassName("彩虹班");
        KgTheme theme = new KgTheme();
        theme.setId(2L);
        theme.setThemeName("安全教育");
        when(classMapper.selectAll()).thenReturn(Collections.singletonList(clazz));
        when(themeMapper.selectAll()).thenReturn(Collections.singletonList(theme));

        MetadataExtractResult extracted = new MetadataExtractResult();
        extracted.setResourceType(ResourceType.DOCUMENT);
        extracted.setMimeType("text/plain");
        extracted.setMetadataJson("{}");
        extracted.setSearchableText("test");
        when(metadataExtractService.extract(any())).thenReturn(extracted);
        when(storageManager.upload(anyString(), any(), anyLong(), anyString()))
                .thenReturn(new StorageResult("local", "k1", "/storage/k1"));
        when(classMapper.selectOptionsByResourceId(anyLong())).thenReturn(Collections.singletonList(new SimpleOptionVO()));
        when(themeMapper.selectOptionsByResourceId(anyLong())).thenReturn(Collections.singletonList(new SimpleOptionVO()));
        when(tagMapper.selectOptionsByResourceId(anyLong())).thenReturn(Collections.emptyList());
        when(favoriteMapper.exists(anyLong(), anyLong())).thenReturn(0);

        AtomicLong seq = new AtomicLong(100);
        doAnswer(invocation -> {
            KgResource entity = invocation.getArgument(0);
            entity.setId(seq.getAndIncrement());
            return 1;
        }).when(resourceMapper).insert(any(KgResource.class));
        when(resourceMapper.selectById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            KgResource r = new KgResource();
            r.setId(id);
            r.setUploaderId(2L);
            r.setVisibility("PRIVATE");
            return r;
        });
        when(resourceMapper.selectVoById(anyLong())).thenAnswer(invocation -> {
            ResourceVO vo = new ResourceVO();
            vo.setId(invocation.getArgument(0));
            return vo;
        });

        MockMultipartFile valid = new MockMultipartFile(
                "files",
                "彩虹班_安全教育_2026-03-12.txt",
                "text/plain",
                "ok".getBytes()
        );
        MockMultipartFile invalid = new MockMultipartFile(
                "files",
                "坏文件名.txt",
                "text/plain",
                "bad".getBytes()
        );
        ResourceUploadDTO dto = new ResourceUploadDTO();
        dto.setVisibility("PRIVATE");
        dto.setDescription("d");
        dto.setClassIds("1");
        dto.setThemeIds("2");
        dto.setActivityDate(LocalDate.of(2026, 3, 12));

        AuthUserDetails user = new AuthUserDetails(2L, "teacher_a", "", "李老师", 1L, 1, Collections.singletonList("TEACHER"));
        UploadBatchResultVO result = resourceService.upload(new MockMultipartFile[]{valid, invalid}, dto, user);

        Assertions.assertEquals(2, result.getTotalCount());
        Assertions.assertEquals(2, result.getSuccessCount());
        Assertions.assertEquals(0, result.getFailCount());
        Assertions.assertEquals(2, result.getSuccessList().size());
        Assertions.assertEquals(0, result.getFailedList().size());
        verify(resourceMapper, times(2)).insert(any(KgResource.class));
    }
}
