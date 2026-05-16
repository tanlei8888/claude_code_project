package com.hedgehog.tag;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体，对应 blog_tag 表。
 */
@Data
@TableName("blog_tag")
public class BlogTag {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 标签名称 */
    private String name;
    /** URL 标识，全局唯一 */
    private String slug;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;
}
