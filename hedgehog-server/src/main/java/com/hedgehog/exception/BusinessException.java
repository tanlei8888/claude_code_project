package com.hedgehog.exception;

import com.hedgehog.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常类。
 *
 * <p>用于抛出预期内的业务错误（如用户名不存在、权限不足、数据校验失败等），
 * 被 {@link GlobalExceptionHandler} 捕获后以 warn 级别记录并返回错误响应。
 *
 * @see GlobalExceptionHandler
 * @see ResultCode
 */
@Getter
public class BusinessException extends RuntimeException {
    /** 业务错误码 */
    private final int code;

    /**
     * 使用自定义错误码和消息创建业务异常。
     *
     * @param code    业务错误码
     * @param message 错误描述
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用错误码枚举创建业务异常。
     *
     * @param resultCode 错误码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用默认错误码（500）创建业务异常。
     *
     * @param message 错误描述
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
}
