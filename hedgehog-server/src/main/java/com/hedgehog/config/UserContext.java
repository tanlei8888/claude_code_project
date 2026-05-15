package com.hedgehog.config;

/**
 * 请求级用户上下文，基于 ThreadLocal 实现线程隔离。
 *
 * <p>由 {@link JwtAuthFilter} 在请求进入时设置 userId 和 role，
 * 请求结束后通过 {@link #remove()} 清理，防止内存泄漏和跨请求数据污染。
 * Service 层和 Controller 层通过静态方法直接获取当前用户信息。
 *
 * @see JwtAuthFilter
 */
public class UserContext {
    /** 当前请求的用户 ID */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    /** 当前请求的用户角色 */
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setRole(String role) {
        ROLE.set(role);
    }

    public static String getRole() {
        return ROLE.get();
    }

    /**
     * 清除当前线程的 ThreadLocal 数据。
     * 必须在请求结束时调用，防止内存泄漏。
     */
    public static void remove() {
        USER_ID.remove();
        ROLE.remove();
    }
}
