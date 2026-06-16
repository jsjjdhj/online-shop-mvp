package com.shop.service.impl;

import com.shop.common.BusinessException;
import com.shop.dto.LoginRequest;
import com.shop.dto.LoginResponse;
import com.shop.dto.RegisterRequest;
import com.shop.entity.User;
import com.shop.repository.UserRepository;
import com.shop.service.UserService;
import com.shop.util.JwtUtil;
import com.shop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    private static final int MAX_FAIL_COUNT = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    @Override
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("该用户名已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtil.encode(request.getPassword()));
        user.setRole("USER");
        user.setStatus(0);
        user.setFailCount(0);
        userRepository.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 检查账号是否锁定
        if (user.getStatus() == 1 && user.getLockTime() != null) {
            if (LocalDateTime.now().isBefore(user.getLockTime())) {
                throw new BusinessException("账号已锁定，请15分钟后再试");
            }
            // 锁定到期，自动解锁
            user.setStatus(0);
            user.setFailCount(0);
            user.setLockTime(null);
            userRepository.updateById(user);
        }

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            // 登录失败，记录失败次数
            int newFailCount = user.getFailCount() + 1;
            user.setFailCount(newFailCount);
            if (newFailCount >= MAX_FAIL_COUNT) {
                user.setStatus(1);
                user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userRepository.updateById(user);
                throw new BusinessException("账号已锁定，请15分钟后再试");
            }
            userRepository.updateById(user);
            throw new BusinessException("用户名或密码错误");
        }

        // 登录成功，重置失败计数
        user.setFailCount(0);
        user.setStatus(0);
        user.setLockTime(null);
        userRepository.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    @Override
    public User getCurrentUser(Long userId) {
        return userRepository.selectById(userId);
    }
}
