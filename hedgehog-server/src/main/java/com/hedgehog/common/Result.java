package com.hedgehog.common;

import lombok.Data;

/**
 * 统一 API 响应包装类。
 *
 * <p>所有 Controller 返回值均通过此类包装，确保前端收到的 JSON
 * 结构一致：{@code { code: int, message: string, data: T }}。
 *
 * @param <T> 响应数据的类型
 */
@Data
public class Result<T> {
    /** 业务状态码，200 表示成功 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 响应数据，可为 null */
    private T data;

    /**
     * 创建成功响应（含数据）。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    /**
     * 创建成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 创建失败响应。
     *
     * @param code    业务错误码
     * @param message 错误描述
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    /**
     * 通过错误码枚举创建失败响应。
     *
     * @param resultCode 错误码枚举
     * @param <T>        数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 创建默认失败响应（code=500）。
     *
     * @param message 错误描述
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
}
