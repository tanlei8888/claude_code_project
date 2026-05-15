package com.hedgehog.common;

import lombok.Getter;

/**
 * 统一业务错误码枚举。
 *
 * <p>错误码分段约定：
 * <ul>
 *   <li>200：成功</li>
 *   <li>400/401/403/404：通用 HTTP 状态码</li>
 *   <li>1001~1999：用户相关（登录、注册、权限）</li>
 *   <li>2001~2099：分类相关</li>
 *   <li>2101~2199：标签相关</li>
 *   <li>2201~2299：文章相关</li>
 *   <li>2301~2399：评论相关</li>
 *   <li>2401~2499：文件/媒体相关</li>
 *   <li>2501~2599：点赞相关</li>
 *   <li>500：系统异常</li>
 * </ul>
 */
@Getter
public enum ResultCode {
    /** 成功 */
    SUCCESS(200, "success"),
    /** 未登录或 token 失效 */
    UNAUTHORIZED(401, "请先登录"),
    /** 无管理员权限 */
    FORBIDDEN(403, "无权限访问"),
    /** 请求的资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    /** 请求参数校验失败 */
    PARAM_ERROR(400, "参数校验失败"),
    /** 用户名或密码错误 */
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    /** 用户名已被注册 */
    USERNAME_EXISTS(1002, "用户名已存在"),
    /** 用户不存在 */
    USER_NOT_FOUND(1003, "用户不存在"),
    /** 用户已被禁用 */
    USER_DISABLED(1004, "用户已被禁用"),
    /** 分类不存在 */
    CATEGORY_NOT_FOUND(2001, "分类不存在"),
    /** 分类 slug 已存在 */
    CATEGORY_SLUG_EXISTS(2002, "分类标识已存在"),
    /** 分类下存在文章，不允许删除 */
    CATEGORY_HAS_ARTICLES(2003, "分类下存在文章，无法删除"),
    /** 标签不存在 */
    TAG_NOT_FOUND(2101, "标签不存在"),
    /** 标签 slug 已存在 */
    TAG_SLUG_EXISTS(2102, "标签标识已存在"),
    /** 文章不存在 */
    ARTICLE_NOT_FOUND(2201, "文章不存在"),
    /** 文章 slug 已存在 */
    ARTICLE_SLUG_EXISTS(2202, "文章标识已存在"),
    /** 评论不存在 */
    COMMENT_NOT_FOUND(2301, "评论不存在"),
    /** 媒体文件不存在 */
    MEDIA_NOT_FOUND(2401, "媒体文件不存在"),
    /** 文件上传失败 */
    FILE_UPLOAD_FAILED(2402, "文件上传失败"),
    /** 文件类型不支持 */
    UNSUPPORTED_FILE_TYPE(2403, "不支持的文件类型，仅允许 jpeg/png/gif/webp/svg"),
    /** 文件大小超过 10MB 限制 */
    FILE_SIZE_EXCEEDED(2404, "文件大小超过限制，最大 10MB"),
    /** 已点过赞 */
    ALREADY_LIKED(2501, "已经点过赞了"),
    /** 系统内部异常 */
    SYSTEM_ERROR(500, "系统异常，请稍后重试");

    /** 错误码 */
    private final int code;
    /** 错误消息 */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
