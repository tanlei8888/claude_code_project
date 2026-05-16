package com.hedgehog.auth;

import com.hedgehog.common.Result;
import com.hedgehog.config.UserContext;
import com.hedgehog.media.SysMedia;
import com.hedgehog.user.User;
import com.hedgehog.media.SysMediaService;
import com.hedgehog.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证控制器，处理登录、注册、个人信息相关请求。
 */
@Tag(name = "认证管理", description = "登录、注册、个人信息、头像选择")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final SysMediaService sysMediaService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserService userService, SysMediaService sysMediaService, BCryptPasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.sysMediaService = sysMediaService;
        this.passwordEncoder = passwordEncoder;
    }

    /** POST /api/auth/login — 用户登录 */
    @Operation(summary = "用户登录", description = "用户名+密码登录，返回 JWT 令牌")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    /** POST /api/auth/register — 用户注册（默认角色 USER） */
    @Operation(summary = "用户注册", description = "用户名+密码+昵称注册，默认角色为 USER")
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
    @Operation(summary = "获取个人信息", description = "返回当前登录用户的完整信息（不含密码）")
    @GetMapping("/info")
    public Result<User> info() {
        User user = userService.getById(UserContext.getUserId());
        user.setPassword(null);
        return Result.ok(user);
    }

    /** PUT /api/auth/profile — 修改个人信息（昵称/邮箱/头像/简介/手机号/密码） */
    @Operation(summary = "修改个人资料", description = "仅更新传入的非空字段（昵称/邮箱/手机/头像/简介/密码）")
    @PutMapping("/profile")
    public Result<?> profile(@RequestBody Map<String, String> body) {
        User user = userService.getById(UserContext.getUserId());
        if (body.containsKey("nickname")) user.setNickname(body.get("nickname"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("avatar")) user.setAvatar(body.get("avatar"));
        if (body.containsKey("bio")) user.setBio(body.get("bio"));
        if (StringUtils.hasText(body.get("password"))) {
            user.setPassword(passwordEncoder.encode(body.get("password")));
        }
        userService.updateById(user);
        return Result.ok();
    }

    /** GET /api/auth/avatars — 获取所有用户可用头像（需登录） */
    @Operation(summary = "获取可选头像列表", description = "返回所有 mediaType=AVATAR 的头像供用户选择")
    @GetMapping("/avatars")
    public Result<List<SysMedia>> avatars() {
        return Result.ok(sysMediaService.getAvatars());
    }
}
