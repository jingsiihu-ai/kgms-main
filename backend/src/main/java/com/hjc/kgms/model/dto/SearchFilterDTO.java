package com.hjc.kgms.model.dto;

import lombok.Data;

@Data
public class SearchFilterDTO {
    private String keyword;
    private Long categoryId;
    private String applicableGrade;
    private String semester;
    private String resourceFormat;
    private String tagName;
    private Integer pageNo = 1;
    private Integer pageSize = 12;
    private String sortBy = "created_at";
    private String sortOrder = "desc";
}
