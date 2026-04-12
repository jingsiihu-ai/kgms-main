package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ThemeDTO {
    @NotBlank(message = "主题名称不能为空")
    private String themeName;
    private String description;
}
