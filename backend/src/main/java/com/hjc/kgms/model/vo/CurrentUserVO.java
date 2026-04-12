package com.hjc.kgms.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserVO {
    private Long id;
    private String username;
    private String name;
    private Integer roleId;
    private List<String> roles;
}
