package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.ArticlePageRequest;
import com.hedgehog.entity.ArticleSaveRequest;
import com.hedgehog.entity.BlogArticle;

public interface BlogArticleService extends IService<BlogArticle> {
    IPage<BlogArticle> pagePublished(ArticlePageRequest request);
    IPage<BlogArticle> pageAll(ArticlePageRequest request);
    BlogArticle getBySlug(String slug);
    BlogArticle getByIdWithDetails(Long id);
    void save(ArticleSaveRequest request);
    void update(ArticleSaveRequest request);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void toggleTop(Long id);
    void publishScheduled();
}
