package com.hedgehog.service;

import com.hedgehog.entity.LoginRequest;
import com.hedgehog.entity.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
