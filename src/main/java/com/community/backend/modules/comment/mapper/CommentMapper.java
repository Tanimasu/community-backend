package com.community.backend.modules.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.modules.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
