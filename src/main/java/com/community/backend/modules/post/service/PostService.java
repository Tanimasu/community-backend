package com.community.backend.modules.post.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.post.dto.CreatePostRequest;
import com.community.backend.modules.post.dto.PostResponse;
import com.community.backend.modules.post.entity.Post;
import com.community.backend.modules.post.mapper.PostMapper;
import com.community.backend.modules.user.dto.UserBriefResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostMapper postMapper;
    private final UserService userService;

    public PostService(PostMapper postMapper, UserService userService) {
        this.postMapper = postMapper;
        this.userService = userService;
    }

    public PostResponse createPost(Long userId, CreatePostRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCommentCount(0);
        post.setLikeCount(0);
        postMapper.insert(post);
        return getPost(post.getId());
    }

    public PageResult<PostResponse> listPosts(PageQuery pageQuery) {
        // 按 id 倒序就是按发布时间倒序，而且直接走主键索引
        Page<Post> page = postMapper.selectPage(pageQuery.toPage(),
                new LambdaQueryWrapper<Post>().orderByDesc(Post::getId));

        List<Post> posts = page.getRecords();
        // 一次查出这一页所有作者，而不是每个帖子查一次用户表（避免 N+1 查询）
        Map<Long, User> authors = userService.getUserMap(posts.stream().map(Post::getUserId).toList());
        List<PostResponse> list = posts.stream()
                .map(post -> PostResponse.from(post, UserBriefResponse.from(authors.get(post.getUserId()))))
                .toList();
        return PageResult.of(page, list);
    }

    public PostResponse getPost(Long id) {
        Post post = getPostOrThrow(id);
        User author = userService.getUserById(post.getUserId());
        return PostResponse.from(post, UserBriefResponse.from(author));
    }

    public Post getPostOrThrow(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "帖子不存在");
        }
        return post;
    }

    public void increaseCommentCount(Long postId) {
        // UPDATE post SET comment_count = comment_count + 1 WHERE id = ?
        // 在数据库里原子地 +1，而不是先查出来、Java 里加 1、再写回去
        postMapper.update(new LambdaUpdateWrapper<Post>()
                .setSql("comment_count = comment_count + 1")
                .eq(Post::getId, postId));
    }
}
