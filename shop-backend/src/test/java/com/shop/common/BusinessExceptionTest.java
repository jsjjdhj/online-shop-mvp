package com.shop.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    // BE-01: 构造_带code — code=403, message="test"
    @Test
    void constructor_WithCode() {
        BusinessException e = new BusinessException(403, "no permission");
        assertEquals(403, e.getCode());
        assertEquals("no permission", e.getMessage());
    }

    // BE-02: 构造_不带code — code默认400
    @Test
    void constructor_DefaultCode() {
        BusinessException e = new BusinessException("bad request");
        assertEquals(400, e.getCode());
        assertEquals("bad request", e.getMessage());
    }

    // BE-03: 继承自 RuntimeException — 可被 catch
    @Test
    void isRuntimeException() {
        Exception e = assertThrows(RuntimeException.class,
                () -> { throw new BusinessException(500, "test"); });
        assertTrue(e instanceof BusinessException);
    }
}
