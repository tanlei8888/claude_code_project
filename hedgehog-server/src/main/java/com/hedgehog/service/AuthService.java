package com.hedgehog.service;

import com.hedgehog.entity.LoginRequest;
import com.hedgehog.entity.LoginResponse;

/**
 * 认证服务接口。
 */
public interface AuthService {
    /**
     * 用户登录，校验用户名和密码并返回 JWT token。
     *
     * @param request 登录请求（用户名 + 密码）
     * @return 登录响应（token + 用户信息）
     * @throws com.hedgehog.exception.BusinessException 用户名不存在、密码错误或用户被禁用时抛出
     */
    LoginResponse login(LoginRequest request);
}
