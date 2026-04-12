package com.hjc.kgms;

import com.hjc.kgms.enums.ResourceType;
import com.hjc.kgms.service.impl.MetadataExtractServiceImpl;
import com.hjc.kgms.util.MetadataExtractResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

class MetadataExtractServiceImplTest {

    @Test
    void shouldExtractDocumentMetadataAndText() {
        MetadataExtractServiceImpl service = new MetadataExtractServiceImpl();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lesson.txt",
                "text/plain",
                "这是幼儿园教案关键词：春季活动安全教育".getBytes(StandardCharsets.UTF_8)
        );

        MetadataExtractResult result = service.extract(file);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ResourceType.DOCUMENT, result.getResourceType());
        Assertions.assertTrue(result.getSearchableText().contains("春季活动"));
        Assertions.assertTrue(result.getMetadataJson().contains("lesson.txt"));
    }
}
