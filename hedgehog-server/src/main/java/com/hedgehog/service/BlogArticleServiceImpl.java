package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.config.UserContext;
import com.hedgehog.entity.*;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.BlogArticleMapper;
import com.hedgehog.mapper.BlogArticleTagMapper;
import com.hedgehog.mapper.UserMapper;
import com.hedgehog.util.MarkdownUtil;
import com.hedgehog.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现。
 */
@Service
public class BlogArticleServiceImpl extends ServiceImpl<BlogArticleMapper, BlogArticle> implements BlogArticleService {

    private final BlogArticleTagMapper articleTagMapper;
    private final BlogTagService tagService;
    private final BlogCategoryService categoryService;
    private final BlogLikeService likeService;
    private final UserMapper userMapper;

    public BlogArticleServiceImpl(BlogArticleTagMapper articleTagMapper, BlogTagService tagService,
                                  BlogCategoryService categoryService, BlogLikeService likeService,
                                  UserMapper userMapper) {
        this.articleTagMapper = articleTagMapper;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.likeService = likeService;
        this.userMapper = userMapper;
    }

    @Override
    public IPage<BlogArticle> pagePublished(ArticlePageRequest request) {
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
        // 仅返回已发布文章
        wrapper.eq(BlogArticle::getStatus, 1);
        if (request.getCategoryId() != null) {
            wrapper.eq(BlogArticle::getCategoryId, request.getCategoryId());
        }
        if (request.getTagId() != null) {
            // 通过关联表查询标签对应的文章 ID
            List<Long> articleIds = articleTagMapper.selectList(
                    new LambdaQueryWrapper<BlogArticleTag>().eq(BlogArticleTag::getTagId, request.getTagId())
            ).stream().map(BlogArticleTag::getArticleId).collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                return new Page<>(request.getPage(), request.getSize());
            }
            wrapper.in(BlogArticle::getId, articleIds);
        }
        if (StringUtils.hasText(request.getKeyword())) {
            // 标题和摘要联合模糊搜索
            wrapper.and(w -> w.like(BlogArticle::getTitle, request.getKeyword())
                    .or().like(BlogArticle::getSummary, request.getKeyword()));
        }
        // 置顶优先，再按创建时间倒序
        wrapper.orderByDesc(BlogArticle::getIsTop).orderByDesc(BlogArticle::getCreateTime);
        Page<BlogArticle> page = new Page<>(request.getPage(), request.getSize());
        IPage<BlogArticle> result = baseMapper.selectPage(page, wrapper);
        // 批量填充分类、标签、作者信息
        enrichArticles(result.getRecords());
        return result;
    }

    @Override
    public IPage<BlogArticle> pageAll(ArticlePageRequest request) {
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
        if (request.getStatus() != null) {
            wrapper.eq(BlogArticle::getStatus, request.getStatus());
        }
        if (request.getCategoryId() != null) {
            wrapper.eq(BlogArticle::getCategoryId, request.getCategoryId());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(BlogArticle::getTitle, request.getKeyword());
        }
        wrapper.orderByDesc(BlogArticle::getIsTop).orderByDesc(BlogArticle::getCreateTime);
        Page<BlogArticle> page = new Page<>(request.getPage(), request.getSize());
        IPage<BlogArticle> result = baseMapper.selectPage(page, wrapper);
        enrichArticles(result.getRecords());
        return result;
    }

    @Override
    public BlogArticle getBySlug(String slug) {
        BlogArticle article = getOne(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getSlug, slug));
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        // 浏览量+1
        article.setViewCount(article.getViewCount() + 1);
        baseMapper.updateById(article);
        enrichArticles(Collections.singletonList(article));
        // 查询当前用户是否点赞
        Long userId = UserContext.getUserId();
        if (userId != null) {
            Map<Long, Boolean> status = likeService.getLikeStatus(Collections.singleton(article.getId()));
            article.setLiked(status.getOrDefault(article.getId(), false));
        }
        return article;
    }

    @Override
    public BlogArticle getByIdWithDetails(Long id) {
        BlogArticle article = super.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        enrichArticles(Collections.singletonList(article));
        return article;
    }

    @Override
    @Transactional
    public void save(ArticleSaveRequest request) {
        // slug 为空时由中文标题自动生成拼音
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getTitle()));
        }
        BlogArticle exist = getOne(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getSlug, request.getSlug()));
        if (exist != null) {
            throw new BusinessException(ResultCode.ARTICLE_SLUG_EXISTS);
        }
        BlogArticle article = new BlogArticle();
        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setSummary(request.getSummary());
        article.setContentMd(request.getContentMd());
        // Markdown → HTML 转换
        article.setContentHtml(MarkdownUtil.render(request.getContentMd()));
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        article.setIsTop(request.getIsTop() != null ? request.getIsTop() : 0);
        article.setPublishTime(request.getPublishTime());
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCommentCount(0L);
        article.setAuthorId(UserContext.getUserId());
        baseMapper.insert(article);
        // 保存文章-标签关联
        saveTags(article.getId(), request.getTagIds());
    }

    @Override
    @Transactional
    public void update(ArticleSaveRequest request) {
        BlogArticle article = super.getById(request.getId());
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getTitle()));
        }
        // 检查 slug 唯一性（排除自身）
        BlogArticle exist = getOne(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getSlug, request.getSlug())
                .ne(BlogArticle::getId, request.getId()));
        if (exist != null) {
            throw new BusinessException(ResultCode.ARTICLE_SLUG_EXISTS);
        }
        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setSummary(request.getSummary());
        article.setContentMd(request.getContentMd());
        article.setContentHtml(MarkdownUtil.render(request.getContentMd()));
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus() != null ? request.getStatus() : article.getStatus());
        article.setIsTop(request.getIsTop() != null ? request.getIsTop() : 0);
        article.setPublishTime(request.getPublishTime());
        baseMapper.updateById(article);
        // 更新标签关联：先删后插
        articleTagMapper.delete(new LambdaQueryWrapper<BlogArticleTag>()
                .eq(BlogArticleTag::getArticleId, article.getId()));
        saveTags(article.getId(), request.getTagIds());
    }

    @Override
    public void delete(Long id) {
        BlogArticle article = super.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        super.removeById(id);
        // 级联删除文章-标签关联
        articleTagMapper.delete(new LambdaQueryWrapper<BlogArticleTag>()
                .eq(BlogArticleTag::getArticleId, id));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        BlogArticle article = super.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        article.setStatus(status);
        // 直接发布时记录发布时间
        if (status == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        baseMapper.updateById(article);
    }

    @Override
    public void toggleTop(Long id) {
        BlogArticle article = super.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        // 置顶状态取反
        article.setIsTop(article.getIsTop() == 1 ? 0 : 1);
        baseMapper.updateById(article);
    }

    @Override
    public void publishScheduled() {
        // 查询状态为"定时发布"且发布时间已到的文章
        List<BlogArticle> articles = list(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getStatus, 2)
                .le(BlogArticle::getPublishTime, LocalDateTime.now()));
        for (BlogArticle article : articles) {
            article.setStatus(1);
            baseMapper.updateById(article);
        }
    }

    /**
     * 批量保存文章-标签关联。
     */
    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                BlogArticleTag at = new BlogArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    /**
     * 为文章列表批量填充分类、标签和作者信息。
     * 采用批量查询避免 N+1 问题。
     */
    private void enrichArticles(List<BlogArticle> articles) {
        if (articles.isEmpty()) return;
        Set<Long> articleIds = articles.stream().map(BlogArticle::getId).collect(Collectors.toSet());
        Set<Long> categoryIds = articles.stream().map(BlogArticle::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> authorIds = articles.stream().map(BlogArticle::getAuthorId).collect(Collectors.toSet());

        // 批量查分类
        Map<Long, BlogCategory> categoryMap = Collections.emptyMap();
        if (!categoryIds.isEmpty()) {
            categoryMap = categoryService.listByIds(categoryIds).stream()
                    .collect(Collectors.toMap(BlogCategory::getId, c -> c));
        }

        // 批量查作者（脱敏：清除密码字段）
        Map<Long, User> authorMap = Collections.emptyMap();
        if (!authorIds.isEmpty()) {
            authorMap = userMapper.selectBatchIds(authorIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> {
                        u.setPassword(null);
                        return u;
                    }));
        }

        // 批量查标签
        List<BlogArticleTag> allAT = articleTagMapper.selectList(
                new LambdaQueryWrapper<BlogArticleTag>().in(BlogArticleTag::getArticleId, articleIds));
        Map<Long, List<Long>> articleTagMap = allAT.stream().collect(Collectors.groupingBy(
                BlogArticleTag::getArticleId, Collectors.mapping(BlogArticleTag::getTagId, Collectors.toList())));
        Set<Long> allTagIds = allAT.stream().map(BlogArticleTag::getTagId).collect(Collectors.toSet());
        Map<Long, BlogTag> tagMap = Collections.emptyMap();
        if (!allTagIds.isEmpty()) {
            tagMap = tagService.listByIds(allTagIds).stream()
                    .collect(Collectors.toMap(BlogTag::getId, t -> t));
        }

        // 组装填充
        for (BlogArticle article : articles) {
            article.setCategory(categoryMap.get(article.getCategoryId()));
            article.setAuthor(authorMap.get(article.getAuthorId()));
            List<Long> tagIds = articleTagMap.getOrDefault(article.getId(), Collections.emptyList());
            article.setTags(tagIds.stream().map(tagMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }
}
