package com.hjc.kgms.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceVO {
    private Long id;
    private String title;
    private String description;
    private String url;
    private Long userId;
    private String uploaderName;
    private String status;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer resourceType;
    private String resourceFormat;
    private String fileName;
    private Long fileSize;
    private Long categoryId;
    private String categoryName;
    private String categoryPath;
    private String applicableGrade;
    private String semester;
    private LocalDateTime createdAt;
    private List<SimpleOptionVO> tags;
    private Boolean favorite;
}
