package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.TagSaveRequest;
import com.hedgehog.service.BlogTagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TagController {

    private final BlogTagService tagService;

    public TagController(BlogTagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public Result<?> list() {
        return Result.ok(tagService.listAll());
    }

    @AdminRequired
    @PostMapping("/admin/tags")
    public Result<?> create(@RequestBody TagSaveRequest request) {
        tagService.save(request);
        return Result.ok();
    }

    @AdminRequired
    @PutMapping("/admin/tags/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody TagSaveRequest request) {
        request.setId(id);
        tagService.update(request);
        return Result.ok();
    }

    @AdminRequired
    @DeleteMapping("/admin/tags/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.ok();
    }
}
