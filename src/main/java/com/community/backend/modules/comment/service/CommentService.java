package com.community.backend.modules.comment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.modules.comment.dto.CommentResponse;
import com.community.backend.modules.comment.dto.CreateCommentRequest;
import com.community.backend.modules.comment.entity.Comment;
import com.community.backend.modules.comment.mapper.CommentMapper;
import com.community.backend.modules.post.service.PostService;
import com.community.backend.modules.user.dto.UserBriefResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserService userService;

    public CommentService(CommentMapper commentMapper, PostService postService, UserService userService) {
        this.commentMapper = commentMapper;
        this.postService = postService;
        this.userService = userService;
    }

    // 插入评论和帖子评论数 +1 要么都成功，要么都回滚
    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CreateCommentRequest request) {
        postService.getPostOrThrow(postId);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.content());
        comment.setLikeCount(0);
        commentMapper.insert(comment);

        postService.increaseCommentCount(postId);

        Comment saved = commentMapper.selectById(comment.getId());
        return CommentResponse.from(saved, UserBriefResponse.from(userService.getUserById(userId)));
    }

    public PageResult<CommentResponse> listComments(Long postId, PageQuery pageQuery) {
        postService.getPostOrThrow(postId);

        // 评论按时间正序，先发的在前面；(post_id, id) 联合索引正好覆盖这个查询
        Page<Comment> page = commentMapper.selectPage(pageQuery.toPage(),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByAsc(Comment::getId));

        List<Comment> comments = page.getRecords();
        Map<Long, User> authors = userService.getUserMap(comments.stream().map(Comment::getUserId).toList());
        List<CommentResponse> list = comments.stream()
                .map(comment -> CommentResponse.from(comment, UserBriefResponse.from(authors.get(comment.getUserId()))))
                .toList();
        return PageResult.of(page, list);
    }
}
