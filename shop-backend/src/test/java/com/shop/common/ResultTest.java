package com.shop.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    // R-01: success(data) — code=200, message="操作成功", data=传入值
    @Test
    void success_WithData() {
        String data = "testData";
        Result<String> result = Result.success(data);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(data, result.getData());
    }

    // R-02: error(code, msg) — code=传入值, message=传入值, data=null
    @Test
    void error_CodeAndMessage() {
        Result<Void> result = Result.error(403, "forbidden");
        assertEquals(403, result.getCode());
        assertEquals("forbidden", result.getMessage());
        assertNull(result.getData());
    }

    // R-03: forbidden(msg) — code=403
    @Test
    void forbidden_Message() {
        Result<Void> result = Result.forbidden("no access");
        assertEquals(403, result.getCode());
        assertEquals("no access", result.getMessage());
        assertNull(result.getData());
    }

    // R-04: success() 无参数 — data=null
    @Test
    void success_NoData() {
        Result<Void> result = Result.success();
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    // R-05: error(msg) 默认 code=400
    @Test
    void error_DefaultCode400() {
        Result<Void> result = Result.error("bad request");
        assertEquals(400, result.getCode());
        assertEquals("bad request", result.getMessage());
    }

    // R-06: unauthorized(msg) — code=401
    @Test
    void unauthorized_Message() {
        Result<Void> result = Result.unauthorized("not logged in");
        assertEquals(401, result.getCode());
    }
}
