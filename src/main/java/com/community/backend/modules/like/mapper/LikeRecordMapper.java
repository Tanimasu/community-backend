package com.community.backend.modules.like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.backend.modules.like.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
}
