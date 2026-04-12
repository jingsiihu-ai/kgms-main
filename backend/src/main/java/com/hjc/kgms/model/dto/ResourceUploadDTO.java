package com.hjc.kgms.model.dto;

import lombok.Data;

@Data
public class ResourceUploadDTO {
    private String title;
    private String description;
    private String status;
    private Long categoryId;
    private String applicableGrade;
    private String semester;
    private String tagIds;
}
