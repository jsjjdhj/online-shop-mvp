package com.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.shop.ShopApplication.class)
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtUtil.generateToken(1L, "admin", "ADMIN");
        userToken = jwtUtil.generateToken(2L, "user", "USER");
    }

    // 辅助方法：为请求添加模拟的 JWT 属性（绕过真实拦截器，直接设置 request attribute）
    private MockHttpServletRequestBuilder withAdminAuth(MockHttpServletRequestBuilder builder) {
        return builder
                .header("Authorization", "Bearer " + adminToken)
                .requestAttr("userId", 1L)
                .requestAttr("username", "admin")
                .requestAttr("role", "ADMIN");
    }

    private MockHttpServletRequestBuilder withUserAuth(MockHttpServletRequestBuilder builder) {
        return builder
                .header("Authorization", "Bearer " + userToken)
                .requestAttr("userId", 2L)
                .requestAttr("username", "user")
                .requestAttr("role", "USER");
    }

    // AT-01: createProduct_管理员成功 — HTTP 200 + Result.code=200
    @Test
    void createProduct_AdminSuccess() throws Exception {
        String json = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "TestProduct",
                "description", "desc",
                "price", new BigDecimal("99.00"),
                "stock", 100
        ));
        mockMvc.perform(withAdminAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // AT-02: createProduct_无权限 — role=USER → BusinessException(403)
    @Test
    void createProduct_NoPermission() throws Exception {
        String json = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "TestProduct2",
                "description", "desc",
                "price", new BigDecimal("99.00"),
                "stock", 10
        ));
        mockMvc.perform(withUserAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message", containsString("无管理员权限")));
    }

    // AT-03: createProduct_参数缺失 — name 为空 → 400 Bad Request
    @Test
    void createProduct_MissingName() throws Exception {
        String json = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "",
                "price", new BigDecimal("99.00"),
                "stock", 10
        ));
        mockMvc.perform(withAdminAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // AT-04: createProduct_价格非法 — price=0 → 400
    @Test
    void createProduct_InvalidPrice() throws Exception {
        String json = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "BadPrice",
                "price", new BigDecimal("0"),
                "stock", 10
        ));
        mockMvc.perform(withAdminAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("价格")));
    }

    // AT-05: updateProduct_成功
    @Test
    void updateProduct_Success() throws Exception {
        // 先创建一个商品
        String createJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "ToUpdate",
                "description", "old desc",
                "price", new BigDecimal("50.00"),
                "stock", 20
        ));
        String response = mockMvc.perform(withAdminAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // 更新（假设 id=1 或从列表中获取）
        String updateJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "UpdatedName",
                "description", "new desc",
                "price", new BigDecimal("88.00"),
                "stock", 30
        ));
        mockMvc.perform(withAdminAuth(put("/api/admin/products/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // AT-06: updateProduct_无权限
    @Test
    void updateProduct_NoPermission() throws Exception {
        String json = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "X", "price", new BigDecimal("99"), "stock", 5
        ));
        mockMvc.perform(withUserAuth(put("/api/admin/products/999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    // AT-07: deleteProduct_成功
    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(withAdminAuth(delete("/api/admin/products/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // AT-08: deleteProduct_无权限
    @Test
    void deleteProduct_NoPermission() throws Exception {
        mockMvc.perform(withUserAuth(delete("/api/admin/products/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    // AT-09: listProducts_无需权限 — 潜在安全漏洞验证
    @Test
    void listProducts_NoAuthRequired() throws Exception {
        // 此端点没有 checkAdmin，即使不传 token 也应返回结果（记录安全漏洞）
        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk());
    }

    // AT-10: listOrders_按状态筛选
    @Test
    void listOrders_ByStatus() throws Exception {
        mockMvc.perform(withAdminAuth(get("/api/admin/orders")
                        .param("status", "0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // AT-11: listOrders_无权限
    @Test
    void listOrders_NoPermission() throws Exception {
        mockMvc.perform(withUserAuth(get("/api/admin/orders")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    // AT-12: getOrderDetail_含商品明细
    @Test
    void getOrderDetail_Success() throws Exception {
        mockMvc.perform(withAdminAuth(get("/api/admin/orders/1")))
                .andExpect(status().isOk());
    }

    // AT-13: confirmOrder_成功
    @Test
    void confirmOrder_Success() throws Exception {
        mockMvc.perform(withAdminAuth(post("/api/admin/orders/1/confirm")))
                .andExpect(status().isOk());
    }

    // AT-14: shipOrder_成功
    @Test
    void shipOrder_Success() throws Exception {
        mockMvc.perform(withAdminAuth(post("/api/admin/orders/1/ship")))
                .andExpect(status().isOk());
    }
}
