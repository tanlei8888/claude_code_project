package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.BlogTag;
import com.hedgehog.entity.TagSaveRequest;

import java.util.List;

public interface BlogTagService extends IService<BlogTag> {
    List<BlogTag> listAll();
    void save(TagSaveRequest request);
    void update(TagSaveRequest request);
    void delete(Long id);
}
