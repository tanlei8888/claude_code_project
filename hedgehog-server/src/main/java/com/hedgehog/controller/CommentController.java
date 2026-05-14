package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.CommentSaveRequest;
import com.hedgehog.service.BlogCommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final BlogCommentService commentService;

    public CommentController(BlogCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public Result<?> list(@RequestParam Long articleId,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.getCommentTree(articleId, page, size));
    }

    @PostMapping("/comments")
    public Result<?> create(@RequestBody CommentSaveRequest request) {
        commentService.create(request);
        return Result.ok();
    }

    @AdminRequired
    @GetMapping("/admin/comments")
    public Result<?> adminList(@RequestParam(required = false) Long articleId,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.pageAll(articleId, status, page, size));
    }

    @AdminRequired
    @PutMapping("/admin/comments/{id}")
    public Result<?> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.audit(id, status);
        return Result.ok();
    }

    @AdminRequired
    @DeleteMapping("/admin/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.ok();
    }
}
