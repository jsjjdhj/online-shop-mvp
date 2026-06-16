package com.shop.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.shop.ShopApplication.class)
class PasswordUtilTest {

    @Autowired private PasswordUtil passwordUtil;

    // PW-01: encode_非哈希结果 — encode("abc") ≠ "abc"
    @Test
    void encode_NotRawString() {
        String encoded = passwordUtil.encode("abc");
        assertNotEquals("abc", encoded);
        assertTrue(encoded.length() > 10);
    }

    // PW-02: matches_正确密码 — encode 后 matches 同一密码 → true
    @Test
    void matches_CorrectPassword() {
        String raw = "MyPassword123";
        String encoded = passwordUtil.encode(raw);
        assertTrue(passwordUtil.matches(raw, encoded));
    }

    // PW-03: matches_错误密码 — 错误密码 → false
    @Test
    void matches_WrongPassword() {
        String encoded = passwordUtil.encode("CorrectPass");
        assertFalse(passwordUtil.matches("WrongPass", encoded));
    }

    // PW-04: encode_含盐随机性 — 同一密码两次 encode 结果不同
    @Test
    void encode_SaltRandomness() {
        String e1 = passwordUtil.encode("samepassword");
        String e2 = passwordUtil.encode("samepassword");
        assertNotEquals(e1, e2);
    }
}
