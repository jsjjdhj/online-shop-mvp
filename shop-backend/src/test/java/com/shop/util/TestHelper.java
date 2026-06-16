package com.shop.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 测试辅助类：生成 JWT Token 用于集成测试
 */
public class TestHelper {

    // 与 application.yml 中 jwt.secret 保持一致
    private static final String SECRET = "YOUR_BASE64_ENCODED_SECRET_KEY_HERE";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            SECRET.length() < 32
                    ? padKey(SECRET)
                    : SECRET.getBytes(StandardCharsets.UTF_8));

    private static byte[] padKey(String key) {
        byte[] original = key.getBytes(StandardCharsets.UTF_8);
        byte[] padded = new byte[Math.max(original.length, 32)];
        System.arraycopy(original, 0, padded, 0, original.length);
        return padded;
    }

    public static String createUserToken(Long userId, String username) {
        return createToken(userId, username, "USER");
    }

    public static String createAdminToken(Long userId, String username) {
        return createToken(userId, username, "ADMIN");
    }

    private static String createToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 3600000))
                .signWith(KEY)
                .compact();
    }
}
