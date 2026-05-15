package com.hedgehog.entity;

import lombok.Data;

/**
 * 文章分页查询请求 DTO。
 */
@Data
public class ArticlePageRequest {
    /** 页码，默认第 1 页 */
    private Integer page = 1;
    /** 每页条数，默认 10 */
    private Integer size = 10;
    /** 文章状态筛选：null=全部（管理员）/1=已发布（公开接口） */
    private Integer status;
    /** 按分类 ID 筛选 */
    private Long categoryId;
    /** 按标签 ID 筛选 */
    private Long tagId;
    /** 按标题关键词模糊搜索 */
    private String keyword;
}
