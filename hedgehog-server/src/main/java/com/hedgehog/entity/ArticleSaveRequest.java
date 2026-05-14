package com.hedgehog.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleSaveRequest {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String contentMd;
    private String coverImage;
    private Long categoryId;
    private List<Long> tagIds;
    private Integer status;
    private Integer isTop;
    private LocalDateTime publishTime;
}
