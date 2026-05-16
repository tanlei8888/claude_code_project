package com.hedgehog.like;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Set;

/**
 * 点赞服务接口。
 */
public interface BlogLikeService extends IService<BlogLike> {
    /**
     * 点赞/取消点赞（toggle 模式）。
     * 已点赞则取消（删除记录并扣减 like_count），未点赞则添加。
     *
     * @return true=已点赞，false=已取消
     */
    boolean toggle(Long articleId);

    /**
     * 批量查询当前用户对指定文章的点赞状态。
     *
     * @param articleIds 文章 ID 集合
     * @return articleId → 是否已点赞
     */
    Map<Long, Boolean> getLikeStatus(Set<Long> articleIds);
}
