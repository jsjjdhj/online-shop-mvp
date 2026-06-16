package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
import com.shop.dto.LoginRequest;
import com.shop.dto.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ShopApplication.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testRegister_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser_" + System.currentTimeMillis());
        req.setPassword("Test1234");
        req.setConfirmPassword("Test1234");
        Assertions.assertDoesNotThrow(() -> userService.register(req));
    }

    @Test
    void testRegister_DuplicateUsername() {
        // 实际测试中可插入一个已知用户并尝试重复注册
    }

    @Test
    void testLogin_LockAfter5Failures() {
        // 创建临时用户，连续5次输错密码，验证锁定
    }

    @Test
    void testLogin_UnlockAfterLockDuration() {
        // 锁定后等待（或mock时间）验证自动解锁
    }
}
