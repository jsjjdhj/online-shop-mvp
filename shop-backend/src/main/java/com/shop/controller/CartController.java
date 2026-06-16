package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.CartRequest;
import com.shop.dto.CartUpdateRequest;
import com.shop.entity.Cart;
import com.shop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<Cart>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.listCartItems(userId));
    }

    @PostMapping
    public Result<Void> add(HttpServletRequest request, @Valid @RequestBody CartRequest cartRequest) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addItem(userId, cartRequest);
        return Result.success();
    }

    @PutMapping("/{cartId}")
    public Result<Void> update(HttpServletRequest request,
                               @PathVariable Long cartId,
                               @Valid @RequestBody CartUpdateRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateQuantity(userId, cartId, updateRequest);
        return Result.success();
    }

    @DeleteMapping("/{cartId}")
    public Result<Void> remove(HttpServletRequest request, @PathVariable Long cartId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeItem(userId, cartId);
        return Result.success();
    }
}
