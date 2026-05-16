package com.hedgehog.comment;

import com.baomidou.mybatisplus.annotation.*;
import com.hedgehog.user.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体，对应 blog_comment 表。
 * 支持两层嵌套：顶级评论（parentId=null）+ 二级回复（parentId=顶级评论ID）。
 */
@Data
@TableName("blog_comment")
public class BlogComment {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 文章 ID，外键 blog_article.id */
    private Long articleId;
    /** 评论者用户 ID，外键 sys_user.id */
    private Long userId;
    /** 父评论 ID：null=顶级评论，非 null=二级回复 */
    private Long parentId;
    /** 回复目标用户 ID */
    private Long replyToUserId;
    /** 评论内容 */
    private String content;
    /** 状态：0=待审核 1=已通过 2=已拒绝 */
    private Integer status;
    /** 评论者 IP 地址 */
    private String ipAddress;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;

    /** 非数据库字段：评论者信息 */
    @TableField(exist = false)
    private User user;
    /** 非数据库字段：回复目标用户信息 */
    @TableField(exist = false)
    private User replyToUser;
    /** 非数据库字段：子回复列表 */
    @TableField(exist = false)
    private List<BlogComment> replies;
}
