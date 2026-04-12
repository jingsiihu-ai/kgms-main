package com.hjc.kgms.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private CurrentUserVO user;
}
