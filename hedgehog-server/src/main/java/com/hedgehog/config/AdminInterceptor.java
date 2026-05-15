package com.hedgehog.config;

import com.hedgehog.annotation.AdminRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器。
 *
 * <p>检查被 {@link com.hedgehog.annotation.AdminRequired} 标记的接口，
 * 仅允许 role=ADMIN 的用户访问。注解查找顺序：先方法级别，再类级别。
 * 非管理员用户访问时直接返回 403 JSON 响应。
 *
 * @see com.hedgehog.annotation.AdminRequired
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 非 Controller 方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        // 先在方法上查找注解，再在类上查找
        AdminRequired annotation = hm.getMethodAnnotation(AdminRequired.class);
        if (annotation == null) {
            annotation = hm.getBeanType().getAnnotation(AdminRequired.class);
        }
        // 无需管理员权限，直接放行
        if (annotation == null) {
            return true;
        }
        // 检查当前用户角色
        if (!"ADMIN".equals(UserContext.getRole())) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
            return false;
        }
        return true;
    }
}
