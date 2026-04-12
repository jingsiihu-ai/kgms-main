package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ResourceUpdateDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String description;
    private String status;
    private Long categoryId;
    private String applicableGrade;
    private String semester;
    private List<Long> tagIds;
}
