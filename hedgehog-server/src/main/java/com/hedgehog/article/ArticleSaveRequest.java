package com.hedgehog.article;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章保存请求 DTO，创建和编辑共用。
 */
@Data
public class ArticleSaveRequest {
    /** 文章 ID，编辑时必传，创建时为空 */
    private Long id;
    /** 标题 */
    private String title;
    /** URL 标识，为空时由标题自动生成拼音 slug */
    private String slug;
    /** 摘要 */
    private String summary;
    /** Markdown 原文 */
    private String contentMd;
    /** 封面图 URL */
    private String coverImage;
    /** 分类 ID */
    private Long categoryId;
    /** 关联标签 ID 列表 */
    private List<Long> tagIds;
    /** 状态：0=草稿 1=已发布 2=定时发布 3=私密 */
    private Integer status;
    /** 是否置顶：0=否 1=是 */
    private Integer isTop;
    /** 定时发布时间，status=2 时使用 */
    private LocalDateTime publishTime;
}
