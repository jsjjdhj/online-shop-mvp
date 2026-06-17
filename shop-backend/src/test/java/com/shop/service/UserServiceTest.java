package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
import com.shop.dto.LoginRequest;
import com.shop.dto.RegisterRequest;
import com.shop.entity.User;
import com.shop.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    private static final int MAX_FAIL_COUNT = 5;

    @Test
    void testRegisterAndLogin_Success() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        assertDoesNotThrow(() -> userService.register(r));
        LoginRequest l = new LoginRequest();
        l.setUsername(u); l.setPassword("Test1234");
        var resp = assertDoesNotThrow(() -> userService.login(l));
        assertNotNull(resp.getToken());
        assertEquals(u, resp.getUsername());
    }

    @Test
    void testRegister_Duplicate() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);
        assertThrows(BusinessException.class, () -> userService.register(r));
    }

    @Test
    void testRegister_PasswordMismatch() {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("mm_" + System.nanoTime());
        r.setPassword("Test1234"); r.setConfirmPassword("Mismatch1");
        assertThrows(BusinessException.class, () -> userService.register(r));
    }

    @Test
    void testLogin_WrongPassword() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);
        LoginRequest l = new LoginRequest();
        l.setUsername(u); l.setPassword("WrongPwd1");
        assertThrows(BusinessException.class, () -> userService.login(l));
    }

    @Test
    void testLogin_LockAfter5Failures() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);
        LoginRequest w = new LoginRequest();
        w.setUsername(u); w.setPassword("WrongPwd1");
        for (int i = 0; i < 4; i++) {
            assertThrows(BusinessException.class, () -> userService.login(w));
        }
        BusinessException e = assertThrows(BusinessException.class, () -> userService.login(w));
        assertTrue(e.getMessage().contains("已锁定"));
    }

    @Test
    void testLogin_LockedAccountCannotLogin() {
        // 先锁定账号
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);

        // 连续5次失败登录锁定账号
        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername(u);
        wrongRequest.setPassword("WrongPwd");
        for (int i = 0; i < MAX_FAIL_COUNT; i++) {
            try {
                userService.login(wrongRequest);
            } catch (BusinessException ignored) {
            }
        }

        // 账号已锁定，即使密码正确也不能登录
        LoginRequest correctRequest = new LoginRequest();
        correctRequest.setUsername(u);
        correctRequest.setPassword("Test1234");
        BusinessException e = assertThrows(BusinessException.class, () -> userService.login(correctRequest));
        assertTrue(e.getMessage().contains("已锁定"));
    }

    @Test
    void testLogin_FailCountIncrementsCorrectly() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);

        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername(u);
        wrongRequest.setPassword("WrongPwd");

        // 验证失败次数递增
        for (int i = 1; i < MAX_FAIL_COUNT; i++) {
            assertThrows(BusinessException.class, () -> userService.login(wrongRequest));
            User user = userRepository.findByUsername(u).orElseThrow();
            assertEquals(i, user.getFailCount());
            assertEquals(0, user.getStatus()); // 未锁定
        }

        // 第5次失败后账号锁定
        assertThrows(BusinessException.class, () -> userService.login(wrongRequest));
        User user = userRepository.findByUsername(u).orElseThrow();
        assertEquals(MAX_FAIL_COUNT, user.getFailCount());
        assertEquals(1, user.getStatus()); // 已锁定
        assertNotNull(user.getLockTime());
    }

    @Test
    void testLogin_SuccessResetsFailCount() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);

        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername(u);
        wrongRequest.setPassword("WrongPwd");

        // 失败3次
        for (int i = 0; i < 3; i++) {
            assertThrows(BusinessException.class, () -> userService.login(wrongRequest));
        }

        // 验证失败次数为3
        User user = userRepository.findByUsername(u).orElseThrow();
        assertEquals(3, user.getFailCount());

        // 正确密码登录成功
        LoginRequest correctRequest = new LoginRequest();
        correctRequest.setUsername(u);
        correctRequest.setPassword("Test1234");
        assertDoesNotThrow(() -> userService.login(correctRequest));

        // 验证失败次数重置为0
        user = userRepository.findByUsername(u).orElseThrow();
        assertEquals(0, user.getFailCount());
        assertEquals(0, user.getStatus());
        assertNull(user.getLockTime());
    }

    @Test
    void testLogin_AutoUnlockAfterLockDuration() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);

        // 锁定账号
        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername(u);
        wrongRequest.setPassword("WrongPwd");
        for (int i = 0; i < MAX_FAIL_COUNT; i++) {
            try {
                userService.login(wrongRequest);
            } catch (BusinessException ignored) {
            }
        }

        // 手动设置锁定时间为过去，模拟锁定到期
        User user = userRepository.findByUsername(u).orElseThrow();
        user.setLockTime(LocalDateTime.now().minusMinutes(1));
        userRepository.updateById(user);

        // 现在应该可以正常登录
        LoginRequest correctRequest = new LoginRequest();
        correctRequest.setUsername(u);
        correctRequest.setPassword("Test1234");
        var resp = assertDoesNotThrow(() -> userService.login(correctRequest));
        assertNotNull(resp.getToken());

        // 验证账号已自动解锁
        user = userRepository.findByUsername(u).orElseThrow();
        assertEquals(0, user.getStatus());
        assertEquals(0, user.getFailCount());
        assertNull(user.getLockTime());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonExistentUser" + System.nanoTime());
        request.setPassword("anyPassword");
        BusinessException e = assertThrows(BusinessException.class, () -> userService.login(request));
        assertTrue(e.getMessage().contains("用户名或密码错误"));
    }

    @Test
    void testGetCurrentUser_NotFound() {
        User user = userService.getCurrentUser(999999L);
        assertNull(user);
    }

    // ===== UT-7: getCurrentUser =====
    @Test
    void testGetCurrentUser_Success() {
        String u = "usr_" + System.nanoTime();
        RegisterRequest r = new RegisterRequest();
        r.setUsername(u); r.setPassword("Test1234"); r.setConfirmPassword("Test1234");
        userService.register(r);
        User saved = userRepository.findByUsername(u).get();
        User current = userService.getCurrentUser(saved.getId());
        assertNotNull(current);
        assertEquals(u, current.getUsername());
    }
}
