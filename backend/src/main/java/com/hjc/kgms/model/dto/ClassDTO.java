package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClassDTO {
    @NotBlank(message = "班级名称不能为空")
    private String className;
    private String grade;
    private String description;
}
