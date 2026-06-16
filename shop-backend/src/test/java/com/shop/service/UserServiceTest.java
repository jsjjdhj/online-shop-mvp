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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

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
