package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 文章-标签关联实体，对应 blog_article_tag 关联表。
 * 通过 UNIQUE(article_id, tag_id) 保证同一文章不重复关联同一标签。
 */
@Data
@TableName("blog_article_tag")
public class BlogArticleTag {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 文章 ID，外键 blog_article.id */
    private Long articleId;
    /** 标签 ID，外键 blog_tag.id */
    private Long tagId;
}
