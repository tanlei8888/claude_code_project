package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("blog_article")
public class BlogArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String contentMd;
    private String contentHtml;
    private String coverImage;
    private Long categoryId;
    private Integer status;
    private Integer isTop;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime publishTime;
    private Long authorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<BlogTag> tags;
    @TableField(exist = false)
    private BlogCategory category;
    @TableField(exist = false)
    private User author;
    @TableField(exist = false)
    private Boolean liked;
}
