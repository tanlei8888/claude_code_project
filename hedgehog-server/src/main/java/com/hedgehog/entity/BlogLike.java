package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞实体，对应 blog_like 表。
 * 通过 UNIQUE(article_id, user_id) 保证同一用户对同一文章只能点赞一次。
 */
@Data
@TableName("blog_like")
public class BlogLike {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 文章 ID，外键 blog_article.id */
    private Long articleId;
    /** 用户 ID，外键 sys_user.id */
    private Long userId;
    /** 点赞时间 */
    private LocalDateTime createTime;
}
