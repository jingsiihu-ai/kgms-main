package com.hjc.kgms.controller;

import com.hjc.kgms.common.ApiResponse;
import com.hjc.kgms.model.dto.UserCreateDTO;
import com.hjc.kgms.model.dto.UserUpdateDTO;
import com.hjc.kgms.model.entity.SysRole;
import com.hjc.kgms.model.vo.UserVO;
import com.hjc.kgms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }
    @GetMapping("/users") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<List<UserVO>> listUsers(@RequestParam(value = "keyword", required = false) String keyword) { return ApiResponse.ok(userService.listUsers(keyword)); }
    @PostMapping("/users") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<UserVO> createUser(@Validated @RequestBody UserCreateDTO dto) { return ApiResponse.ok(userService.createUser(dto)); }
    @PutMapping("/users/{id}") @PreAuthorize("hasRole('PRINCIPAL')") public ApiResponse<UserVO> updateUser(@PathVariable("id") Long id, @Validated @RequestBody UserUpdateDTO dto) { return ApiResponse.ok(userService.updateUser(id, dto)); }
    @GetMapping("/roles") public ApiResponse<List<SysRole>> listRoles() { return ApiResponse.ok(userService.listRoles()); }
}
