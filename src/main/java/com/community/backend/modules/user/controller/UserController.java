package com.community.backend.modules.user.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.user.dto.UserResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import com.community.backend.security.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @AuthenticationPrincipal 取到的就是 JwtAuthenticationFilter 里放进去的 LoginUser
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.success(UserResponse.from(findUser(loginUser.id())));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(UserResponse.from(findUser(id)));
    }

    private User findUser(Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
