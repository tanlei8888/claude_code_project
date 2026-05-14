package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.CategorySaveRequest;
import com.hedgehog.service.BlogCategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final BlogCategoryService categoryService;

    public CategoryController(BlogCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public Result<?> list() {
        return Result.ok(categoryService.listAll());
    }

    @AdminRequired
    @PostMapping("/admin/categories")
    public Result<?> create(@RequestBody CategorySaveRequest request) {
        categoryService.save(request);
        return Result.ok();
    }

    @AdminRequired
    @PutMapping("/admin/categories/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody CategorySaveRequest request) {
        request.setId(id);
        categoryService.update(request);
        return Result.ok();
    }

    @AdminRequired
    @DeleteMapping("/admin/categories/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
