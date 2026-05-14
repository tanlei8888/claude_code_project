package com.hedgehog.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数校验失败"),
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_DISABLED(1004, "用户已被禁用"),
    CATEGORY_NOT_FOUND(2001, "分类不存在"),
    CATEGORY_SLUG_EXISTS(2002, "分类标识已存在"),
    CATEGORY_HAS_ARTICLES(2003, "分类下存在文章，无法删除"),
    TAG_NOT_FOUND(2101, "标签不存在"),
    TAG_SLUG_EXISTS(2102, "标签标识已存在"),
    ARTICLE_NOT_FOUND(2201, "文章不存在"),
    ARTICLE_SLUG_EXISTS(2202, "文章标识已存在"),
    COMMENT_NOT_FOUND(2301, "评论不存在"),
    MEDIA_NOT_FOUND(2401, "媒体文件不存在"),
    FILE_UPLOAD_FAILED(2402, "文件上传失败"),
    UNSUPPORTED_FILE_TYPE(2403, "不支持的文件类型，仅允许 jpeg/png/gif/webp/svg"),
    FILE_SIZE_EXCEEDED(2404, "文件大小超过限制，最大 10MB"),
    ALREADY_LIKED(2501, "已经点过赞了"),
    SYSTEM_ERROR(500, "系统异常，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
