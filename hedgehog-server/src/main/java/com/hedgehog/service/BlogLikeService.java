package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.BlogLike;

import java.util.Map;
import java.util.Set;

public interface BlogLikeService extends IService<BlogLike> {
    boolean toggle(Long articleId);
    Map<Long, Boolean> getLikeStatus(Set<Long> articleIds);
}
