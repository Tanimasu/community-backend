package com.community.backend.modules.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.modules.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
