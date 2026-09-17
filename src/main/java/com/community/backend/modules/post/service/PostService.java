package com.community.backend.modules.post.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.like.entity.LikeTargetType;
import com.community.backend.modules.like.service.LikeService;
import com.community.backend.modules.post.dto.CreatePostRequest;
import com.community.backend.modules.post.dto.PostResponse;
import com.community.backend.modules.post.entity.Post;
import com.community.backend.modules.post.mapper.PostMapper;
import com.community.backend.modules.user.dto.UserBriefResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostMapper postMapper;
    private final UserService userService;
    private final LikeService likeService;
    private final PostCacheService postCacheService;

    public PostService(PostMapper postMapper,
                       UserService userService,
                       LikeService likeService,
                       PostCacheService postCacheService) {
        this.postMapper = postMapper;
        this.userService = userService;
        this.likeService = likeService;
        this.postCacheService = postCacheService;
    }

    public PostResponse createPost(Long userId, CreatePostRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCommentCount(0);
        post.setLikeCount(0);
        postMapper.insert(post);
        return getPost(post.getId(), userId);
    }

    // currentUserId 为 null 表示游客
    public PageResult<PostResponse> listPosts(PageQuery pageQuery, Long currentUserId) {
        // 按 id 倒序就是按发布时间倒序，而且直接走主键索引
        Page<Post> page = postMapper.selectPage(pageQuery.toPage(),
                new LambdaQueryWrapper<Post>().orderByDesc(Post::getId));

        List<Post> posts = page.getRecords();
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        // 一次查出这一页所有作者，而不是每个帖子查一次用户表（避免 N+1 查询）
        Map<Long, User> authors = userService.getUserMap(posts.stream().map(Post::getUserId).toList());
        Set<Long> likedPostIds = likeService.getLikedTargetIds(currentUserId, LikeTargetType.POST, postIds);

        List<PostResponse> list = posts.stream()
                .map(post -> PostResponse.from(post, UserBriefResponse.from(authors.get(post.getUserId())))
                        .withLiked(likedPostIds.contains(post.getId())))
                .toList();
        return PageResult.of(page, list);
    }

    public PostResponse getPost(Long id, Long currentUserId) {
        PostResponse post = postCacheService.getOrLoad(id, () -> loadPost(id));
        if (post == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "帖子不存在");
        }
        return post.withLiked(currentUserId != null && likeService.isLiked(currentUserId, LikeTargetType.POST, id));
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
        postCacheService.evictAfterCommit(postId);
    }

    // 缓存未命中时从数据库加载，帖子不存在返回 null
    private PostResponse loadPost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            return null;
        }
        User author = userService.getUserById(post.getUserId());
        return PostResponse.from(post, UserBriefResponse.from(author));
    }
}
