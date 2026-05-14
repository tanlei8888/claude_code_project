package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.BlogComment;
import com.hedgehog.entity.CommentSaveRequest;

import java.util.List;

public interface BlogCommentService extends IService<BlogComment> {
    List<BlogComment> getCommentTree(Long articleId, int page, int size);
    IPage<BlogComment> pageAll(Long articleId, Integer status, int page, int size);
    void create(CommentSaveRequest request);
    void audit(Long id, Integer status);
    void delete(Long id);
}
