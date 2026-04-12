package com.hjc.kgms.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRoleUpdateDTO {
    @NotNull(message = "请选择角色")
    private Integer roleId;
}
