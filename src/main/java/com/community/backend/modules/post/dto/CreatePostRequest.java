package com.community.backend.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题最长 100 个字符")
        String title,

        @NotBlank(message = "内容不能为空")
        @Size(max = 10000, message = "内容最长 10000 个字符")
        String content
) {
}
