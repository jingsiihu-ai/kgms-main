package com.hjc.kgms.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class FilenameMetaParser {

    public FilenameParseResult parse(String originalFilename) {
        FilenameParseResult result = new FilenameParseResult();
        if (StringUtils.isBlank(originalFilename)) {
            result.setStructureValid(false);
            result.setStructureError("文件名为空，无法解析");
            return result;
        }

        String baseName = trimExtension(originalFilename).trim();
        String[] parts = baseName.split("_", -1);
        if (parts.length != 3) {
            result.setStructureValid(false);
            result.setStructureError("文件名格式不正确，应为：{班级名}_{活动主题}_{创建日期}");
            return result;
        }

        String className = parts[0].trim();
        String themeName = parts[1].trim();
        String datePart = parts[2].trim();
        if (StringUtils.isBlank(className) || StringUtils.isBlank(themeName) || StringUtils.isBlank(datePart)) {
            result.setStructureValid(false);
            result.setStructureError("文件名格式不正确，班级名/活动主题/创建日期不能为空");
            return result;
        }

        result.setStructureValid(true);
        result.setClassName(className);
        result.setThemeName(themeName);

        try {
            result.setActivityDate(LocalDate.parse(datePart));
        } catch (DateTimeParseException ex) {
            result.setDateError("创建日期格式错误，仅支持 yyyy-MM-dd");
        }
        return result;
    }

    private String trimExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx <= 0) {
            return filename;
        }
        return filename.substring(0, idx);
    }
}
