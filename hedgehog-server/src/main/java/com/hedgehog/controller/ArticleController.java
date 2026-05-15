package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.ArticlePageRequest;
import com.hedgehog.entity.ArticleSaveRequest;
import com.hedgehog.service.BlogArticleService;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器，处理公开和管理端的文章相关请求。
 */
@RestController
@RequestMapping("/api")
public class ArticleController {

    private final BlogArticleService articleService;

    public ArticleController(BlogArticleService articleService) {
        this.articleService = articleService;
    }

    /** GET /api/articles — 公开文章列表（仅已发布） */
    @GetMapping("/articles")
    public Result<?> list(ArticlePageRequest request) {
        return Result.ok(articleService.pagePublished(request));
    }

    /** GET /api/articles/{slug} — 文章详情（浏览量+1） */
    @GetMapping("/articles/{slug}")
    public Result<?> detail(@PathVariable String slug) {
        return Result.ok(articleService.getBySlug(slug));
    }

    /** GET /api/admin/articles — 管理员文章列表（含草稿/定时/私密） */
    @AdminRequired
    @GetMapping("/admin/articles")
    public Result<?> adminList(ArticlePageRequest request) {
        return Result.ok(articleService.pageAll(request));
    }

    /** GET /api/admin/articles/{id} — 管理员查看文章详情 */
    @AdminRequired
    @GetMapping("/admin/articles/{id}")
    public Result<?> adminDetail(@PathVariable Long id) {
        return Result.ok(articleService.getByIdWithDetails(id));
    }

    /** POST /api/admin/articles — 创建文章 */
    @AdminRequired
    @PostMapping("/admin/articles")
    public Result<?> create(@RequestBody ArticleSaveRequest request) {
        articleService.save(request);
        return Result.ok();
    }

    /** PUT /api/admin/articles/{id} — 更新文章 */
    @AdminRequired
    @PutMapping("/admin/articles/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ArticleSaveRequest request) {
        request.setId(id);
        articleService.update(request);
        return Result.ok();
    }

    /** DELETE /api/admin/articles/{id} — 删除文章 */
    @AdminRequired
    @DeleteMapping("/admin/articles/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }

    /** PUT /api/admin/articles/{id}/status — 修改文章状态 */
    @AdminRequired
    @PutMapping("/admin/articles/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        articleService.updateStatus(id, status);
        return Result.ok();
    }

    /** PUT /api/admin/articles/{id}/top — 切换置顶 */
    @AdminRequired
    @PutMapping("/admin/articles/{id}/top")
    public Result<?> toggleTop(@PathVariable Long id) {
        articleService.toggleTop(id);
        return Result.ok();
    }
}
