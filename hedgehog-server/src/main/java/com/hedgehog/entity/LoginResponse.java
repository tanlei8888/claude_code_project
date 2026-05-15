package com.hedgehog.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 DTO。
 */
@Data
@Builder
public class LoginResponse {
    /** JWT token */
    private String token;
    /** 用户 ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 角色：ADMIN / USER */
    private String role;
}
