package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hedgehog.common.ResultCode;
import com.hedgehog.entity.LoginRequest;
import com.hedgehog.entity.LoginResponse;
import com.hedgehog.entity.User;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        // 统一返回"用户名或密码错误"，防止用户枚举
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        // BCrypt 密码校验
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        String role = user.getRole() != null ? user.getRole() : "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(role)
                .build();
    }
}
