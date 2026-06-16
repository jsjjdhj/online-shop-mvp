package com.shop.controller;

import com.shop.common.PageResult;
import com.shop.common.Result;
import com.shop.dto.ProductRequest;
import com.shop.entity.Orders;
import com.shop.entity.Product;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    // ---- 商品管理 ----

    @PostMapping("/products")
    public Result<Void> createProduct(HttpServletRequest request,
                                      @Valid @RequestBody ProductRequest productRequest) {
        checkAdmin(request);
        productService.createProduct(productRequest);
        return Result.success();
    }

    @PutMapping("/products/{id}")
    public Result<Void> updateProduct(HttpServletRequest request,
                                      @PathVariable Long id,
                                      @Valid @RequestBody ProductRequest productRequest) {
        checkAdmin(request);
        productService.updateProduct(id, productRequest);
        return Result.success();
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        checkAdmin(request);
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.listProducts(page, size, keyword));
    }

    // ---- 订单管理 ----

    @PostMapping("/orders/{orderId}/confirm")
    public Result<Void> confirmOrder(HttpServletRequest request, @PathVariable Long orderId) {
        checkAdmin(request);
        orderService.confirmOrder(orderId);
        return Result.success();
    }

    @PostMapping("/orders/{orderId}/ship")
    public Result<Void> shipOrder(HttpServletRequest request, @PathVariable Long orderId) {
        checkAdmin(request);
        orderService.shipOrder(orderId);
        return Result.success();
    }

    @GetMapping("/orders")
    public Result<PageResult<Orders>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(new PageResult<>(0, page, size, java.util.List.of()));
    }

    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new com.shop.common.BusinessException(403, "无管理员权限");
        }
    }
}
