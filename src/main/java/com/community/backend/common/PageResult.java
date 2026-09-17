package com.community.backend.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public record PageResult<T>(long page, long pageSize, long total, List<T> list) {

    public static <T> PageResult<T> of(IPage<?> page, List<T> list) {
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }
}
