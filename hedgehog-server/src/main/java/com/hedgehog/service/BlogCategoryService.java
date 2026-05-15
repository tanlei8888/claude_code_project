package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.BlogCategory;
import com.hedgehog.entity.CategorySaveRequest;

import java.util.List;

/**
 * 分类服务接口。
 */
public interface BlogCategoryService extends IService<BlogCategory> {
    /**
     * 获取全部分类列表（按排序值升序）。
     */
    List<BlogCategory> listAll();

    /**
     * 创建分类，slug 为空时由名称自动生成拼音。
     */
    void save(CategorySaveRequest request);

    /**
     * 更新分类。
     */
    void update(CategorySaveRequest request);

    /**
     * 删除分类，分类下有文章时拒绝删除。
     */
    void delete(Long id);
}
