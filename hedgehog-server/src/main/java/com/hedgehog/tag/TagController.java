package com.hedgehog.tag;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 标签控制器，处理公开和管理端的标签相关请求。
 */
@Tag(name = "标签管理", description = "文章标签 CRUD")
@RestController
@RequestMapping("/api")
public class TagController {

    private final BlogTagService tagService;

    public TagController(BlogTagService tagService) {
        this.tagService = tagService;
    }

    /** GET /api/tags — 获取全部标签列表 */
    @GetMapping("/tags")
    public Result<?> list() {
        return Result.ok(tagService.listAll());
    }

    /** POST /api/admin/tags — 创建标签 */
    @AdminRequired
    @PostMapping("/admin/tags")
    public Result<?> create(@RequestBody TagSaveRequest request) {
        tagService.save(request);
        return Result.ok();
    }

    /** PUT /api/admin/tags/{id} — 更新标签 */
    @AdminRequired
    @PutMapping("/admin/tags/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody TagSaveRequest request) {
        request.setId(id);
        tagService.update(request);
        return Result.ok();
    }

    /** DELETE /api/admin/tags/{id} — 删除标签 */
    @AdminRequired
    @DeleteMapping("/admin/tags/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.ok();
    }
}
