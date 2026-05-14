package com.hedgehog.entity;

import lombok.Data;

@Data
public class CommentSaveRequest {
    private Long articleId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
}
