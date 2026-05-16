package com.hedgehog.comment;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器，处理公开和管理端的评论相关请求。
 */
@Tag(name = "评论管理", description = "评论列表、发表、审核、删除")
@RestController
@RequestMapping("/api")
public class CommentController {

    private final BlogCommentService commentService;

    public CommentController(BlogCommentService commentService) {
        this.commentService = commentService;
    }

    /** GET /api/comments — 获取文章评论树（仅已审核通过的） */
    @GetMapping("/comments")
    public Result<?> list(@RequestParam Long articleId,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.getCommentTree(articleId, page, size));
    }

    /** POST /api/comments — 发表评论（需登录） */
    @PostMapping("/comments")
    public Result<?> create(@RequestBody CommentSaveRequest request) {
        commentService.create(request);
        return Result.ok();
    }

    /** GET /api/admin/comments — 管理员评论列表（含待审核/已拒绝） */
    @AdminRequired
    @GetMapping("/admin/comments")
    public Result<?> adminList(@RequestParam(required = false) Long articleId,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.pageAll(articleId, status, page, size));
    }

    /** PUT /api/admin/comments/{id} — 审核评论（通过/拒绝） */
    @AdminRequired
    @PutMapping("/admin/comments/{id}")
    public Result<?> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.audit(id, status);
        return Result.ok();
    }

    /** DELETE /api/admin/comments/{id} — 删除评论 */
    @AdminRequired
    @DeleteMapping("/admin/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.ok();
    }
}
