package com.hedgehog.exception;

import com.hedgehog.common.Result;
import com.hedgehog.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器。
 *
 * <p>统一捕获 Controller 层抛出的异常并将其转换为 {@link Result} 响应，
 * 避免异常直接暴露给前端。按异常类型区分日志级别和返回的错误码。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（预期内的错误）。
     *
     * <p>记录 warn 级别日志后返回对应错误码，不打印堆栈。
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 {@code @Valid} 参数校验失败异常。
     *
     * <p>将所有字段的错误信息拼接为分号分隔的字符串返回。
     *
     * @param e 参数校验异常
     * @return 参数错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理文件上传大小超限异常。
     *
     * <p>当上传文件超过 spring.servlet.multipart.max-file-size 配置值时，
     * Spring Boot 会在请求到达 Controller 之前抛出此异常。
     *
     * @param e 文件大小超限异常
     * @return 文件大小超限错误响应（code=2404）
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("上传文件大小超限: {}", e.getMessage());
        return Result.fail(ResultCode.FILE_SIZE_EXCEEDED);
    }

    /**
     * 兜底异常处理，处理所有未被其他 handler 捕获的异常。
     *
     * <p>记录 error 级别日志（含堆栈），返回统一系统错误。
     *
     * @param e 未知异常
     * @return 系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ResultCode.SYSTEM_ERROR);
    }
}
