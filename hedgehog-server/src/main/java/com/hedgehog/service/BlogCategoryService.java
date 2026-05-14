package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.BlogCategory;
import com.hedgehog.entity.CategorySaveRequest;

import java.util.List;

public interface BlogCategoryService extends IService<BlogCategory> {
    List<BlogCategory> listAll();
    void save(CategorySaveRequest request);
    void update(CategorySaveRequest request);
    void delete(Long id);
}
