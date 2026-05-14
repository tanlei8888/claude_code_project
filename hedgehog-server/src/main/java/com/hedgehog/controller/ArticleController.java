package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.ArticlePageRequest;
import com.hedgehog.entity.ArticleSaveRequest;
import com.hedgehog.service.BlogArticleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ArticleController {

    private final BlogArticleService articleService;

    public ArticleController(BlogArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public Result<?> list(ArticlePageRequest request) {
        return Result.ok(articleService.pagePublished(request));
    }

    @GetMapping("/articles/{slug}")
    public Result<?> detail(@PathVariable String slug) {
        return Result.ok(articleService.getBySlug(slug));
    }

    @AdminRequired
    @GetMapping("/admin/articles")
    public Result<?> adminList(ArticlePageRequest request) {
        return Result.ok(articleService.pageAll(request));
    }

    @AdminRequired
    @GetMapping("/admin/articles/{id}")
    public Result<?> adminDetail(@PathVariable Long id) {
        return Result.ok(articleService.getByIdWithDetails(id));
    }

    @AdminRequired
    @PostMapping("/admin/articles")
    public Result<?> create(@RequestBody ArticleSaveRequest request) {
        articleService.save(request);
        return Result.ok();
    }

    @AdminRequired
    @PutMapping("/admin/articles/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ArticleSaveRequest request) {
        request.setId(id);
        articleService.update(request);
        return Result.ok();
    }

    @AdminRequired
    @DeleteMapping("/admin/articles/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }

    @AdminRequired
    @PutMapping("/admin/articles/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        articleService.updateStatus(id, status);
        return Result.ok();
    }

    @AdminRequired
    @PutMapping("/admin/articles/{id}/top")
    public Result<?> toggleTop(@PathVariable Long id) {
        articleService.toggleTop(id);
        return Result.ok();
    }
}
