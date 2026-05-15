package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章实体，对应 blog_article 表。
 */
@Data
@TableName("blog_article")
public class BlogArticle {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 标题 */
    private String title;
    /** URL 标识，全局唯一 */
    private String slug;
    /** 摘要 */
    private String summary;
    /** Markdown 原文 */
    private String contentMd;
    /** 渲染后 HTML */
    private String contentHtml;
    /** 封面图 URL */
    private String coverImage;
    /** 分类 ID，外键 blog_category.id */
    private Long categoryId;
    /** 状态：0=草稿 1=已发布 2=定时发布 3=私密 */
    private Integer status;
    /** 是否置顶：0=否 1=是 */
    private Integer isTop;
    /** 阅读次数 */
    private Long viewCount;
    /** 点赞数（冗余字段，通过 blog_like 表同步） */
    private Long likeCount;
    /** 评论数（冗余字段，通过 blog_comment 表同步） */
    private Long commentCount;
    /** 定时发布时间 */
    private LocalDateTime publishTime;
    /** 作者 ID，外键 sys_user.id */
    private Long authorId;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;

    /** 非数据库字段：关联标签列表 */
    @TableField(exist = false)
    private List<BlogTag> tags;
    /** 非数据库字段：关联分类 */
    @TableField(exist = false)
    private BlogCategory category;
    /** 非数据库字段：关联作者 */
    @TableField(exist = false)
    private User author;
    /** 非数据库字段：当前用户是否已点赞 */
    @TableField(exist = false)
    private Boolean liked;
}
