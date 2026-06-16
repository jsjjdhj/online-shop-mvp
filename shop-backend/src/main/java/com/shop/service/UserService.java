package com.shop.service;

import com.shop.dto.LoginRequest;
import com.shop.dto.LoginResponse;
import com.shop.dto.RegisterRequest;
import com.shop.entity.User;

public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User getCurrentUser(Long userId);
}
