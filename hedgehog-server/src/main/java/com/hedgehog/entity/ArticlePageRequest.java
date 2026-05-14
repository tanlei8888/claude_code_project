package com.hedgehog.entity;

import lombok.Data;

@Data
public class ArticlePageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Integer status;
    private Long categoryId;
    private Long tagId;
    private String keyword;
}
