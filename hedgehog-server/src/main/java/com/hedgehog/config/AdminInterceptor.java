package com.hedgehog.config;

import com.hedgehog.annotation.AdminRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        AdminRequired annotation = hm.getMethodAnnotation(AdminRequired.class);
        if (annotation == null) {
            annotation = hm.getBeanType().getAnnotation(AdminRequired.class);
        }
        if (annotation == null) {
            return true;
        }
        if (!"ADMIN".equals(UserContext.getRole())) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
            return false;
        }
        return true;
    }
}
