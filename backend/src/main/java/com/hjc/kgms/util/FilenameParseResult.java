package com.hjc.kgms.util;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilenameParseResult {
    private boolean structureValid;
    private String structureError;

    private String className;
    private String themeName;

    private LocalDate activityDate;
    private String dateError;
}
