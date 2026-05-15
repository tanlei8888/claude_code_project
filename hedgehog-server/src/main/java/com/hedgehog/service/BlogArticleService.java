package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.ArticlePageRequest;
import com.hedgehog.entity.ArticleSaveRequest;
import com.hedgehog.entity.BlogArticle;

/**
 * 文章服务接口。
 */
public interface BlogArticleService extends IService<BlogArticle> {
    /**
     * 分页查询已发布文章（公开接口），按置顶优先、创建时间倒序排列。
     */
    IPage<BlogArticle> pagePublished(ArticlePageRequest request);

    /**
     * 分页查询全部文章（管理员接口），支持按状态筛选。
     */
    IPage<BlogArticle> pageAll(ArticlePageRequest request);

    /**
     * 通过 slug 获取文章详情，自动增加浏览量并填充关联数据。
     */
    BlogArticle getBySlug(String slug);

    /**
     * 通过 ID 获取文章详情（含分类、标签、作者）。
     */
    BlogArticle getByIdWithDetails(Long id);

    /**
     * 创建文章，自动生成拼音 slug 并将 Markdown 转换为 HTML。
     */
    void save(ArticleSaveRequest request);

    /**
     * 更新文章，同步更新标签关联。
     */
    void update(ArticleSaveRequest request);

    /**
     * 逻辑删除文章。
     */
    void delete(Long id);

    /**
     * 修改文章状态（草稿/已发布/定时发布/私密）。
     */
    void updateStatus(Long id, Integer status);

    /**
     * 切换文章置顶状态。
     */
    void toggleTop(Long id);

    /**
     * 检查并发布到期的定时文章（由定时任务每分钟调用）。
     */
    void publishScheduled();
}
