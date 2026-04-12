package com.hjc.kgms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
    private Long id;
    private String roleCode;
    private String roleName;
}
