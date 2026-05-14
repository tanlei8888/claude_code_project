package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("blog_comment")
public class BlogComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long userId;
    private Long parentId;
    private Long replyToUserId;
    private String content;
    private Integer status;
    private String ipAddress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private User user;
    @TableField(exist = false)
    private User replyToUser;
    @TableField(exist = false)
    private List<BlogComment> replies;
}
