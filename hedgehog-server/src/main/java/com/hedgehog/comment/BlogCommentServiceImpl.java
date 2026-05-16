package com.hedgehog.comment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.config.UserContext;
import com.hedgehog.article.BlogArticle;
import com.hedgehog.user.User;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.user.UserMapper;
import com.hedgehog.article.BlogArticleMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现。
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {

    private final UserMapper userMapper;
    private final BlogArticleMapper articleMapper;

    public BlogCommentServiceImpl(UserMapper userMapper, BlogArticleMapper articleMapper) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public List<BlogComment> getCommentTree(Long articleId, int page, int size) {
        // 分页取顶级评论（parent_id IS NULL）
        Page<BlogComment> p = new Page<>(page, size);
        IPage<BlogComment> topLevel = baseMapper.selectPage(p, new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getArticleId, articleId)
                .eq(BlogComment::getStatus, 1)
                .isNull(BlogComment::getParentId)
                .orderByDesc(BlogComment::getCreateTime));
        List<BlogComment> result = topLevel.getRecords();
        if (result.isEmpty()) return result;
        // 批量取子回复
        Set<Long> parentIds = result.stream().map(BlogComment::getId).collect(Collectors.toSet());
        List<BlogComment> replies = list(new LambdaQueryWrapper<BlogComment>()
                .in(BlogComment::getParentId, parentIds)
                .eq(BlogComment::getStatus, 1)
                .orderByAsc(BlogComment::getCreateTime));
        // 收集所有涉及的用户ID
        Set<Long> userIds = new HashSet<>();
        for (BlogComment c : result) { userIds.add(c.getUserId()); }
        for (BlogComment r : replies) {
            userIds.add(r.getUserId());
            if (r.getReplyToUserId() != null) userIds.add(r.getReplyToUserId());
        }
        Map<Long, User> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) { u.setPassword(null); }
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        // 组装：顶级评论 + 子回复 + 用户信息
        for (BlogComment top : result) {
            top.setUser(userMap.get(top.getUserId()));
            List<BlogComment> children = new ArrayList<>();
            for (BlogComment reply : replies) {
                if (reply.getParentId().equals(top.getId())) {
                    reply.setUser(userMap.get(reply.getUserId()));
                    reply.setReplyToUser(userMap.get(reply.getReplyToUserId()));
                    children.add(reply);
                }
            }
            top.setReplies(children);
        }
        return result;
    }

    @Override
    public IPage<BlogComment> pageAll(Long articleId, Integer status, int page, int size) {
        LambdaQueryWrapper<BlogComment> wrapper = new LambdaQueryWrapper<>();
        if (articleId != null) wrapper.eq(BlogComment::getArticleId, articleId);
        if (status != null) wrapper.eq(BlogComment::getStatus, status);
        wrapper.orderByDesc(BlogComment::getCreateTime);
        Page<BlogComment> p = new Page<>(page, size);
        IPage<BlogComment> result = baseMapper.selectPage(p, wrapper);
        // 批量填充评论者用户信息
        Set<Long> userIds = result.getRecords().stream().map(BlogComment::getUserId).collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) { u.setPassword(null); }
            Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
            for (BlogComment c : result.getRecords()) { c.setUser(userMap.get(c.getUserId())); }
        }
        return result;
    }

    @Override
    public void create(CommentSaveRequest request) {
        BlogArticle article = articleMapper.selectById(request.getArticleId());
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        BlogComment comment = new BlogComment();
        comment.setArticleId(request.getArticleId());
        comment.setUserId(UserContext.getUserId());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());
        // 新评论默认待审核
        comment.setStatus(0);
        baseMapper.insert(comment);
        // 更新文章评论数（仅统计已通过的评论）
        long count = count(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getArticleId, request.getArticleId())
                .eq(BlogComment::getStatus, 1));
        article.setCommentCount(count);
        articleMapper.updateById(article);
    }

    @Override
    public void audit(Long id, Integer status) {
        BlogComment comment = super.getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }
        comment.setStatus(status);
        baseMapper.updateById(comment);
        // 审核后同步更新文章评论数
        long count = count(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getArticleId, comment.getArticleId())
                .eq(BlogComment::getStatus, 1));
        BlogArticle article = articleMapper.selectById(comment.getArticleId());
        if (article != null) {
            article.setCommentCount(count);
            articleMapper.updateById(article);
        }
    }

    @Override
    public void delete(Long id) {
        BlogComment comment = super.getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }
        super.removeById(id);
    }
}
