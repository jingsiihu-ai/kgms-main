package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long id;
    private Long parentId;
    @NotBlank(message = "目录名称不能为空")
    private String name;
    @NotNull(message = "层级不能为空")
    private Integer level;
    private Integer sortOrder = 0;
    private Integer enabled = 1;
}
