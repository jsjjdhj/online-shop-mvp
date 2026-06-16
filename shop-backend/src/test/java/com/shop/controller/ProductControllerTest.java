package com.shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.shop.ShopApplication.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;

    // PCT-01: list_分页搜索 — 返回 PageResult<Product>
    @Test
    void list_PagedSearch() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // PCT-02: detail_存在 — 返回商品对象
    @Test
    void detail_Exists() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // PCT-03: detail_不存在 — ID无效 → 返回错误响应
    @Test
    void detail_NotExists() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    // 验证返回的是结构化 Result 响应（非原始异常）
                    assertTrue(content.contains("\"code\""),
                            "不存在的商品应返回结构化错误 JSON");
                    // code 应该不是 200
                    assertFalse(content.contains("\"code\":200"),
                            "不存在的商品不应返回成功");
                });
    }
}
