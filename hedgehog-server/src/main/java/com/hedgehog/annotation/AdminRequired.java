package com.hedgehog.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限标记注解。
 *
 * <p>标注在 Controller 方法或类上，表示该接口需要管理员（role=ADMIN）才能访问。
 * 由 {@link com.hedgehog.config.AdminInterceptor} 拦截并校验权限，
 * 非管理员用户访问时返回 403。
 *
 * @see com.hedgehog.config.AdminInterceptor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminRequired {
}
