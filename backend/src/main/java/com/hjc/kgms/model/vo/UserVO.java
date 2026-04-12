package com.hjc.kgms.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private Integer roleId;
    private String roleName;
    private String status;
    private LocalDateTime createdAt;
    private List<String> roles;
}
