package com.hjc.kgms.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KgFavorite {
    private Long id;
    private Long userId;
    private Long resourceId;
    private LocalDateTime createdAt;
}
