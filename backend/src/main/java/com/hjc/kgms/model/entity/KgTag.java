package com.hjc.kgms.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KgTag {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
