package com.community.backend.modules.like.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.modules.like.dto.LikeRequest;
import com.community.backend.modules.like.dto.LikeResponse;
import com.community.backend.modules.like.service.LikeService;
import com.community.backend.security.LoginUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ApiResponse<LikeResponse> setLiked(@AuthenticationPrincipal LoginUser loginUser,
                                              @Valid @RequestBody LikeRequest request) {
        return ApiResponse.success(likeService.setLiked(loginUser.id(), request));
    }
}
