package com.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.ShopApplication;
import com.shop.dto.CartRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShopApplication.class)
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddToCart_QuantityExceeds99() throws Exception {
        // 验证数量超过99时返回校验错误
        CartRequest request = new CartRequest();
        request.setProductId(1L);
        request.setQuantity(100);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer test_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
