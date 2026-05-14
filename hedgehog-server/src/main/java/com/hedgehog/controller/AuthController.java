package com.hedgehog.controller;

import com.hedgehog.common.Result;
import com.hedgehog.config.UserContext;
import com.hedgehog.entity.LoginRequest;
import com.hedgehog.entity.LoginResponse;
import com.hedgehog.entity.User;
import com.hedgehog.service.AuthService;
import com.hedgehog.service.UserService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @GetMapping("/info")
    public Result<User> info() {
        User user = userService.getById(UserContext.getUserId());
        user.setPassword(null);
        return Result.ok(user);
    }
}
