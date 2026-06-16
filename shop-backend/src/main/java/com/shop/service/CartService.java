package com.shop.service;

import com.shop.dto.CartRequest;
import com.shop.dto.CartUpdateRequest;
import com.shop.entity.Cart;

import java.util.List;

public interface CartService {

    void addItem(Long userId, CartRequest request);

    void updateQuantity(Long userId, Long cartId, CartUpdateRequest request);

    void removeItem(Long userId, Long cartId);

    List<Cart> listCartItems(Long userId);
}
