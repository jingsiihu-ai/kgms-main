package com.hjc.kgms;

import com.hjc.kgms.util.FilenameMetaParser;
import com.hjc.kgms.util.FilenameParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class FilenameMetaParserTest {

    private final FilenameMetaParser parser = new FilenameMetaParser();

    @Test
    void shouldParseValidFilename() {
        FilenameParseResult result = parser.parse("彩虹班_安全教育_2026-03-12.jpg");
        Assertions.assertTrue(result.isStructureValid());
        Assertions.assertEquals("彩虹班", result.getClassName());
        Assertions.assertEquals("安全教育", result.getThemeName());
        Assertions.assertEquals(LocalDate.of(2026, 3, 12), result.getActivityDate());
    }

    @Test
    void shouldFailWhenSegmentCountInvalid() {
        FilenameParseResult result = parser.parse("彩虹班_2026-03-12.jpg");
        Assertions.assertFalse(result.isStructureValid());
        Assertions.assertNotNull(result.getStructureError());
    }

    @Test
    void shouldReportDateFormatError() {
        FilenameParseResult result = parser.parse("彩虹班_安全教育_20260312.jpg");
        Assertions.assertTrue(result.isStructureValid());
        Assertions.assertNull(result.getActivityDate());
        Assertions.assertEquals("创建日期格式错误，仅支持 yyyy-MM-dd", result.getDateError());
    }
}
