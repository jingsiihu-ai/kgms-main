package com.hjc.kgms.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KgResource {
    private Long id;
    private String title;
    private String description;
    private String url;
    private Long userId;
    private String status;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer resourceType;
    private String resourceFormat;
    private String fileName;
    private Long fileSize;
    private Long categoryId;
    private String applicableGrade;
    private String semester;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
