package com.shop.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.shop.ShopApplication.class)
class JwtUtilTest {

    @Autowired private JwtUtil jwtUtil;

    // JT-01: generateAndParse_RoundTrip — 生成的 token 能被正确解析
    @Test
    void generateAndParse_RoundTrip() {
        String token = jwtUtil.generateToken(1L, "testuser", "USER");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
        assertEquals("USER", claims.get("role"));
    }

    // JT-02: isTokenExpired_Fresh — 新生成的 token 未过期
    @Test
    void isTokenExpired_Fresh() {
        String token = jwtUtil.generateToken(2L, "fresh", "ADMIN");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    // JT-03: isTokenExpired_Tampered — 篡改的 token 判定为过期
    @Test
    void isTokenExpired_Tampered() {
        String token = jwtUtil.generateToken(3L, "real", "USER");
        String tampered = token.substring(0, token.length() - 3) + "xxx";
        assertTrue(jwtUtil.isTokenExpired(tampered));
    }

    // JT-04: parseToken_EmptyString — 空字符串抛出异常
    @Test
    void parseToken_EmptyString() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken(""));
    }

    // JT-05: generateToken_DifferentUsers — 不同 userId 生成不同 token
    @Test
    void generateToken_DifferentUsers() {
        String t1 = jwtUtil.generateToken(100L, "a", "USER");
        String t2 = jwtUtil.generateToken(200L, "b", "USER");
        assertNotEquals(t1, t2);
    }

    // JT-06: parseToken_Claims完整性 — claims 包含 subject/username/role/exp/iat
    @Test
    void parseToken_ClaimsCompleteness() {
        String token = jwtUtil.generateToken(99L, "fulluser", "ADMIN");
        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims.getSubject());
        assertNotNull(claims.get("username"));
        assertNotNull(claims.get("role"));
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
    }
}
