package com.community.backend.modules.comment.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.modules.comment.dto.CommentResponse;
import com.community.backend.modules.comment.dto.CreateCommentRequest;
import com.community.backend.modules.comment.service.CommentService;
import com.community.backend.security.LoginUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<PageResult<CommentResponse>> listComments(@PathVariable Long postId,
                                                                 @RequestParam(defaultValue = "1") long page,
                                                                 @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResponse.success(commentService.listComments(postId, new PageQuery(page, pageSize)));
    }

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@PathVariable Long postId,
                                                      @AuthenticationPrincipal LoginUser loginUser,
                                                      @Valid @RequestBody CreateCommentRequest request) {
        return ApiResponse.success(commentService.createComment(postId, loginUser.id(), request));
    }
}
