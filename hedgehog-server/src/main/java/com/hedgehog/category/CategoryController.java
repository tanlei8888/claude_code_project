package com.hedgehog.category;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 分类控制器，处理公开和管理端的分类相关请求。
 */
@Tag(name = "分类管理", description = "文章分类 CRUD")
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final BlogCategoryService categoryService;

    public CategoryController(BlogCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** GET /api/categories — 获取全部分类列表 */
    @GetMapping("/categories")
    public Result<?> list() {
        return Result.ok(categoryService.listAll());
    }

    /** POST /api/admin/categories — 创建分类 */
    @AdminRequired
    @PostMapping("/admin/categories")
    public Result<?> create(@RequestBody CategorySaveRequest request) {
        categoryService.save(request);
        return Result.ok();
    }

    /** PUT /api/admin/categories/{id} — 更新分类 */
    @AdminRequired
    @PutMapping("/admin/categories/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody CategorySaveRequest request) {
        request.setId(id);
        categoryService.update(request);
        return Result.ok();
    }

    /** DELETE /api/admin/categories/{id} — 删除分类 */
    @AdminRequired
    @DeleteMapping("/admin/categories/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
