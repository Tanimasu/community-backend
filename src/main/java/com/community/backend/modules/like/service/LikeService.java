package com.community.backend.modules.like.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.comment.entity.Comment;
import com.community.backend.modules.comment.mapper.CommentMapper;
import com.community.backend.modules.like.dto.LikeRequest;
import com.community.backend.modules.like.dto.LikeResponse;
import com.community.backend.modules.like.entity.LikeRecord;
import com.community.backend.modules.like.entity.LikeTargetType;
import com.community.backend.modules.like.mapper.LikeRecordMapper;
import com.community.backend.modules.post.entity.Post;
import com.community.backend.modules.post.mapper.PostMapper;
import com.community.backend.modules.post.service.PostCacheService;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 这里直接用 PostMapper / CommentMapper，而不是 PostService / CommentService：
// 因为 PostService、CommentService 要依赖 LikeService 查询"是否已点赞"，反过来再依赖就成了循环依赖
@Service
public class LikeService {

    private final LikeRecordMapper likeRecordMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final PostCacheService postCacheService;

    public LikeService(LikeRecordMapper likeRecordMapper,
                       PostMapper postMapper,
                       CommentMapper commentMapper,
                       PostCacheService postCacheService) {
        this.likeRecordMapper = likeRecordMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.postCacheService = postCacheService;
    }

    // 点赞记录和点赞数在同一个事务里修改，保证两者始终一致
    @Transactional
    public LikeResponse setLiked(Long userId, LikeRequest request) {
        LikeTargetType targetType = request.targetType();
        Long targetId = request.targetId();
        boolean liked = request.liked();

        getLikeCountOrThrow(targetType, targetId);

        // 只有状态真的发生变化时才修改计数：重复点赞、重复取消都不会让计数出错
        boolean changed = liked
                ? addLikeRecord(userId, targetType, targetId)
                : removeLikeRecord(userId, targetType, targetId);
        if (changed) {
            updateLikeCount(targetType, targetId, liked);
        }

        return new LikeResponse(targetType, targetId, liked, getLikeCountOrThrow(targetType, targetId));
    }

    // 批量查询用户点赞过哪些目标，用于列表里显示"是否已点赞"
    public Set<Long> getLikedTargetIds(Long userId, LikeTargetType targetType, Collection<Long> targetIds) {
        if (userId == null || targetIds.isEmpty()) {
            return Set.of();
        }
        return likeRecordMapper.selectList(new LambdaQueryWrapper<LikeRecord>()
                        .select(LikeRecord::getTargetId)
                        .eq(LikeRecord::getUserId, userId)
                        .eq(LikeRecord::getTargetType, targetType)
                        .in(LikeRecord::getTargetId, targetIds))
                .stream()
                .map(LikeRecord::getTargetId)
                .collect(Collectors.toSet());
    }

    public boolean isLiked(Long userId, LikeTargetType targetType, Long targetId) {
        return getLikedTargetIds(userId, targetType, Set.of(targetId)).contains(targetId);
    }

    private boolean addLikeRecord(Long userId, LikeTargetType targetType, Long targetId) {
        LikeRecord record = new LikeRecord();
        record.setUserId(userId);
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        try {
            likeRecordMapper.insert(record);
            return true;
        } catch (DuplicateKeyException e) {
            // 唯一索引 (user_id, target_type, target_id) 挡住了重复点赞，说明之前已经点过
            return false;
        }
    }

    private boolean removeLikeRecord(Long userId, LikeTargetType targetType, Long targetId) {
        return likeRecordMapper.delete(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId)) > 0;
    }

    private void updateLikeCount(LikeTargetType targetType, Long targetId, boolean increase) {
        String setSql = increase ? "like_count = like_count + 1" : "like_count = like_count - 1";
        switch (targetType) {
            case POST -> {
                postMapper.update(new LambdaUpdateWrapper<Post>()
                        .setSql(setSql)
                        .eq(Post::getId, targetId));
                // 点赞数变了，帖子详情缓存要失效
                postCacheService.evictAfterCommit(targetId);
            }
            case COMMENT -> commentMapper.update(new LambdaUpdateWrapper<Comment>()
                    .setSql(setSql)
                    .eq(Comment::getId, targetId));
        }
    }

    private int getLikeCountOrThrow(LikeTargetType targetType, Long targetId) {
        return switch (targetType) {
            case POST -> {
                Post post = postMapper.selectById(targetId);
                if (post == null) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, "帖子不存在");
                }
                yield post.getLikeCount();
            }
            case COMMENT -> {
                Comment comment = commentMapper.selectById(targetId);
                if (comment == null) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, "评论不存在");
                }
                yield comment.getLikeCount();
            }
        };
    }
}
