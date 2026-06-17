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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void updateProduct_Success() throws Exception {
        String createJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "ToUpdate",
                "description", "old desc",
                "price", new BigDecimal("50.00"),
                "stock", 20
        ));
        mockMvc.perform(withAdminAuth(post("/api/admin/products"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk());

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

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(withAdminAuth(delete("/api/admin/products/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteProduct_NoPermission() throws Exception {
        mockMvc.perform(withUserAuth(delete("/api/admin/products/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listProducts_AdminSuccess() throws Exception {
        mockMvc.perform(withAdminAuth(get("/api/admin/products")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listProducts_NoPermission() throws Exception {
        mockMvc.perform(withUserAuth(get("/api/admin/products")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listProducts_NoToken() throws Exception {
        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void listOrders_ByStatus() throws Exception {
        mockMvc.perform(withAdminAuth(get("/api/admin/orders")
                        .param("status", "0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listOrders_NoPermission() throws Exception {
        mockMvc.perform(withUserAuth(get("/api/admin/orders")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void getOrderDetail_Success() throws Exception {
        mockMvc.perform(withAdminAuth(get("/api/admin/orders/1")))
                .andExpect(status().isOk());
    }

    @Test
    void confirmOrder_Success() throws Exception {
        mockMvc.perform(withAdminAuth(post("/api/admin/orders/1/confirm")))
                .andExpect(status().isOk());
    }

    @Test
    void shipOrder_Success() throws Exception {
        mockMvc.perform(withAdminAuth(post("/api/admin/orders/1/ship")))
                .andExpect(status().isOk());
    }
}
