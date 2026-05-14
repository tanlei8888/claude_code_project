package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.BlogArticle;
import com.hedgehog.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    private final BlogArticleService articleService;
    private final BlogCategoryService categoryService;
    private final BlogTagService tagService;
    private final BlogCommentService commentService;

    public DashboardController(BlogArticleService articleService, BlogCategoryService categoryService,
                               BlogTagService tagService, BlogCommentService commentService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.commentService = commentService;
    }

    @AdminRequired
    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        long articleCount = articleService.count();
        long categoryCount = categoryService.count();
        long tagCount = tagService.count();
        long commentCount = commentService.count();
        long totalViews = articleService.list().stream().mapToLong(BlogArticle::getViewCount).sum();
        List<BlogArticle> recentArticles = articleService.lambdaQuery()
                .orderByDesc(BlogArticle::getCreateTime)
                .last("LIMIT 5")
                .list();

        List<Map<String, Object>> recent = new ArrayList<>();
        for (BlogArticle a : recentArticles) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("title", a.getTitle());
            item.put("status", a.getStatus());
            item.put("viewCount", a.getViewCount());
            item.put("createTime", a.getCreateTime());
            recent.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("articleCount", articleCount);
        data.put("categoryCount", categoryCount);
        data.put("tagCount", tagCount);
        data.put("commentCount", commentCount);
        data.put("totalViews", totalViews);
        data.put("recentArticles", recent);
        return Result.ok(data);
    }
}
