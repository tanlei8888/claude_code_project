package com.hedgehog.controller;

import com.hedgehog.common.Result;
import com.hedgehog.config.UserContext;
import com.hedgehog.entity.LoginRequest;
import com.hedgehog.entity.LoginResponse;
import com.hedgehog.entity.User;
import com.hedgehog.service.AuthService;
import com.hedgehog.service.UserService;
import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器，处理登录、注册、个人信息相关请求。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /** POST /api/auth/login — 用户登录 */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    /** POST /api/auth/register — 用户注册（默认角色 USER） */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setStatus(1);
        user.setRole("USER");
        userService.save(user);
        return Result.ok();
    }

    /** GET /api/auth/info — 获取当前登录用户信息 */
    @GetMapping("/info")
    public Result<User> info() {
        User user = userService.getById(UserContext.getUserId());
        user.setPassword(null);
        return Result.ok(user);
    }

    /** PUT /api/auth/profile — 修改个人信息（昵称/邮箱/头像/简介/密码） */
    @PutMapping("/profile")
    public Result<?> profile(@RequestBody Map<String, String> body) {
        User user = userService.getById(UserContext.getUserId());
        if (body.containsKey("nickname")) user.setNickname(body.get("nickname"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("avatar")) user.setAvatar(body.get("avatar"));
        if (body.containsKey("bio")) user.setBio(body.get("bio"));
        if (StringUtils.hasText(body.get("password"))) {
            user.setPassword(passwordEncoder.encode(body.get("password")));
        }
        userService.updateById(user);
        return Result.ok();
    }
}
