package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章分类实体，对应 blog_category 表。
 */
@Data
@TableName("blog_category")
public class BlogCategory {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 分类名称 */
    private String name;
    /** URL 标识，全局唯一 */
    private String slug;
    /** 分类描述 */
    private String description;
    /** 排序值，越小越靠前 */
    private Integer sortOrder;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;
}
