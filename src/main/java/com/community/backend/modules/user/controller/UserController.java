package com.community.backend.modules.user.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<String> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ApiResponse.success("用户创建成功");
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }
}