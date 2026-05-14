package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.entity.BlogTag;
import com.hedgehog.entity.TagSaveRequest;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.BlogTagMapper;
import com.hedgehog.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

    @Override
    public List<BlogTag> listAll() {
        return list(new LambdaQueryWrapper<BlogTag>()
                .orderByDesc(BlogTag::getCreateTime));
    }

    @Override
    public void save(TagSaveRequest request) {
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getName()));
        }
        BlogTag exist = getOne(new LambdaQueryWrapper<BlogTag>()
                .eq(BlogTag::getSlug, request.getSlug()));
        if (exist != null) {
            throw new BusinessException(ResultCode.TAG_SLUG_EXISTS);
        }
        BlogTag entity = new BlogTag();
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        super.save(entity);
    }

    @Override
    public void update(TagSaveRequest request) {
        BlogTag entity = super.getById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        }
        if (!StringUtils.hasText(request.getSlug())) {
            request.setSlug(SlugUtil.toSlug(request.getName()));
        }
        BlogTag exist = getOne(new LambdaQueryWrapper<BlogTag>()
                .eq(BlogTag::getSlug, request.getSlug())
                .ne(BlogTag::getId, request.getId()));
        if (exist != null) {
            throw new BusinessException(ResultCode.TAG_SLUG_EXISTS);
        }
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        super.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        BlogTag entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        }
        super.removeById(id);
    }
}
