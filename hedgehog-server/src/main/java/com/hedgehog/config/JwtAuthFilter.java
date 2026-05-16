package com.hedgehog.config;

import com.hedgehog.util.JwtUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JWT 认证过滤器，在每个请求到达 Controller 前解析 token 并设置用户上下文。
 *
 * <p>处理流程：
 * <ol>
 *   <li>白名单路径（公开接口、静态资源）直接放行</li>
 *   <li>从 Authorization 头提取 Bearer token</li>
 *   <li>解析 token 获取 userId 和 role，存入 {@link UserContext}</li>
 *   <li>请求结束后通过 finally 清除 UserContext，防止内存泄漏</li>
 * </ol>
 *
 * @see UserContext
 * @see com.hedgehog.util.JwtUtil
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 白名单路径跳过 token 校验
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")
                || path.startsWith("/api/articles") || path.startsWith("/api/categories")
                || path.startsWith("/api/tags") || (path.startsWith("/api/comments") && "GET".equals(request.getMethod()))
                || path.startsWith("/api/site/config") || path.startsWith("/api/admin/upload/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> tokenOpt = extractToken(request);
        if (!tokenOpt.isPresent()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return;
        }

        String token = tokenOpt.get();
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.parseToken(token).get("role", String.class);
            UserContext.setUserId(userId);
            UserContext.setRole(role);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期\"}");
        } finally {
            // 确保请求结束后清理 ThreadLocal，防止内存泄漏
            UserContext.remove();
        }
    }

    /**
     * 从请求头中提取 Bearer token。
     *
     * @param request HTTP 请求
     * @return token 字符串（不含 "Bearer " 前缀），无则返回 Optional.empty()
     */
    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }
}
