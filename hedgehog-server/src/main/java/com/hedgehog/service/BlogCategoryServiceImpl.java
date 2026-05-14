package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.entity.BlogCategory;
import com.hedgehog.entity.CategorySaveRequest;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.BlogCategoryMapper;
import com.hedgehog.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BlogCategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory> implements BlogCategoryService {

    @Override
    public List<BlogCategory> listAll() {
        return list(new LambdaQueryWrapper<BlogCategory>()
                .orderByAsc(BlogCategory::getSortOrder));
    }

    @Override
    public void save(CategorySaveRequest request) {
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getName()));
        }
        BlogCategory exist = getOne(new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getSlug, request.getSlug()));
        if (exist != null) {
            throw new BusinessException(ResultCode.CATEGORY_SLUG_EXISTS);
        }
        BlogCategory entity = new BlogCategory();
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        super.save(entity);
    }

    @Override
    public void update(CategorySaveRequest request) {
        BlogCategory entity = super.getById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getName()));
        }
        BlogCategory exist = getOne(new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getSlug, request.getSlug())
                .ne(BlogCategory::getId, request.getId()));
        if (exist != null) {
            throw new BusinessException(ResultCode.CATEGORY_SLUG_EXISTS);
        }
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        super.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        BlogCategory entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        super.removeById(id);
    }
}
