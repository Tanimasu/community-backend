package com.community.backend.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public record PageQuery(long page, long pageSize) {

    private static final long MAX_PAGE_SIZE = 50;

    // 页码至少为 1，每页条数限制在 1-50，防止有人传 pageSize=100000 把数据库拖垮
    public PageQuery {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
    }

    public <T> Page<T> toPage() {
        return Page.of(page, pageSize);
    }
}
