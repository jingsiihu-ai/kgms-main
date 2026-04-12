package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TagDTO {
    @NotBlank(message = "标签名称不能为空")
    private String name;
}
