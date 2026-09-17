package com.community.backend.modules.post.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.modules.post.dto.CreatePostRequest;
import com.community.backend.modules.post.dto.PostResponse;
import com.community.backend.modules.post.service.PostService;
import com.community.backend.security.LoginUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PageResult<PostResponse>> listPosts(@AuthenticationPrincipal LoginUser loginUser,
                                                           @RequestParam(defaultValue = "1") long page,
                                                           @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(postService.listPosts(new PageQuery(page, pageSize), currentUserId(loginUser)));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPost(@AuthenticationPrincipal LoginUser loginUser,
                                             @PathVariable Long id) {
        return ApiResponse.success(postService.getPost(id, currentUserId(loginUser)));
    }

    @PostMapping
    public ApiResponse<PostResponse> createPost(@AuthenticationPrincipal LoginUser loginUser,
                                                @Valid @RequestBody CreatePostRequest request) {
        return ApiResponse.success(postService.createPost(loginUser.id(), request));
    }

    // GET 接口游客也能访问：带了有效 Token 时 loginUser 有值，否则为 null
    private Long currentUserId(LoginUser loginUser) {
        return loginUser != null ? loginUser.id() : null;
    }
}
