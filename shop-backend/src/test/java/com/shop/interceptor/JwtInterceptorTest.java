package com.shop.interceptor;

import com.shop.common.BusinessException;
import com.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.shop.ShopApplication.class)
class JwtInterceptorTest {

    @Autowired private JwtInterceptor jwtInterceptor;
    @Autowired private JwtUtil jwtUtil;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    // JIT-01: preHandle_有效Token — 设置 userId/username/role attribute，返回 true
    @Test
    void preHandle_ValidToken() throws Exception {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        request.addHeader("Authorization", "Bearer " + token);

        boolean result = jwtInterceptor.preHandle(request, response, null);

        assertTrue(result);
        assertEquals(1L, request.getAttribute("userId"));
        assertEquals("admin", request.getAttribute("username"));
        assertEquals("ADMIN", request.getAttribute("role"));
    }

    // JIT-02: preHandle_无Authorization头 — 抛出 BusinessException(401)
    @Test
    void preHandle_NoAuthHeader() {
        BusinessException e = assertThrows(BusinessException.class,
                () -> jwtInterceptor.preHandle(request, response, null));
        assertEquals(401, e.getCode());
        assertTrue(e.getMessage().contains("未登录"));
    }

    // JIT-03: preHandle_非Bearer格式 — 非 Bearer 前缀 → 401
    @Test
    void preHandle_NonBearerFormat() {
        request.addHeader("Authorization", "Token sometoken");
        BusinessException e = assertThrows(BusinessException.class,
                () -> jwtInterceptor.preHandle(request, response, null));
        assertEquals(401, e.getCode());
    }

    // JIT-04: preHandle_无效Token — 篡改/伪造的 token → 异常（JWT库抛出 MalformedJwtException）
    @Test
    void preHandle_InvalidToken() {
        request.addHeader("Authorization", "Bearer invalid.token.here");
        assertThrows(Exception.class,
                () -> jwtInterceptor.preHandle(request, response, null));
    }

    // JIT-05: preHandle_Bearer后为空 — Header = "Bearer "（空token）→ 异常（IllegalArgumentException）
    @Test
    void preHandle_EmptyAfterBearer() {
        request.addHeader("Authorization", "Bearer ");
        assertThrows(Exception.class,
                () -> jwtInterceptor.preHandle(request, response, null));
    }
}
