package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.config.UserContext;
import com.hedgehog.entity.BlogArticle;
import com.hedgehog.entity.BlogLike;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.BlogArticleMapper;
import com.hedgehog.mapper.BlogLikeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike> implements BlogLikeService {

    private final BlogArticleMapper articleMapper;

    public BlogLikeServiceImpl(BlogArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    @Transactional
    public boolean toggle(Long articleId) {
        Long userId = UserContext.getUserId();
        BlogArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        BlogLike exist = getOne(new LambdaQueryWrapper<BlogLike>()
                .eq(BlogLike::getArticleId, articleId)
                .eq(BlogLike::getUserId, userId));
        if (exist != null) {
            baseMapper.deleteById(exist.getId());
            article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
            articleMapper.updateById(article);
            return false;
        } else {
            BlogLike like = new BlogLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            baseMapper.insert(like);
            article.setLikeCount(article.getLikeCount() + 1);
            articleMapper.updateById(article);
            return true;
        }
    }

    @Override
    public Map<Long, Boolean> getLikeStatus(Set<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) return Collections.emptyMap();
        Long userId = UserContext.getUserId();
        if (userId == null) return Collections.emptyMap();
        List<BlogLike> likes = list(new LambdaQueryWrapper<BlogLike>()
                .eq(BlogLike::getUserId, userId)
                .in(BlogLike::getArticleId, articleIds));
        Set<Long> likedIds = likes.stream().map(BlogLike::getArticleId).collect(Collectors.toSet());
        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : articleIds) {
            result.put(id, likedIds.contains(id));
        }
        return result;
    }
}
