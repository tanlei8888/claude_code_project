package com.hedgehog.comment;

import lombok.Data;

/**
 * 评论发表请求 DTO。
 */
@Data
public class CommentSaveRequest {
    /** 文章 ID */
    private Long articleId;
    /** 评论内容 */
    private String content;
    /** 父评论 ID：null=顶级评论，非 null=二级回复 */
    private Long parentId;
    /** 回复目标用户 ID */
    private Long replyToUserId;
}
