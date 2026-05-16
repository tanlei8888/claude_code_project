package com.hedgehog.comment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 评论服务接口。
 */
public interface BlogCommentService extends IService<BlogComment> {
    /**
     * 获取文章评论树（仅已通过审核的评论）。
     * 两层嵌套：分页取顶级评论 + 批量取子回复。
     */
    List<BlogComment> getCommentTree(Long articleId, int page, int size);

    /**
     * 分页查询全部评论（管理员接口），支持按文章和状态筛选。
     */
    IPage<BlogComment> pageAll(Long articleId, Integer status, int page, int size);

    /**
     * 发表评论。
     */
    void create(CommentSaveRequest request);

    /**
     * 审核评论（通过/拒绝）。
     */
    void audit(Long id, Integer status);

    /**
     * 删除评论。
     */
    void delete(Long id);
}
