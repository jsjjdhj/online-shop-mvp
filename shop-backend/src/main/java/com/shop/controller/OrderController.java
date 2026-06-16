package com.shop.controller;

import com.shop.common.PageResult;
import com.shop.common.Result;
import com.shop.dto.OrderSubmitRequest;
import com.shop.entity.OrderItem;
import com.shop.entity.Orders;
import com.shop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Orders> submit(HttpServletRequest request,
                                 @Valid @RequestBody OrderSubmitRequest submitRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Orders order = orderService.submitOrder(userId, submitRequest);
        return Result.success(order);
    }

    @GetMapping
    public Result<PageResult<Orders>> list(HttpServletRequest request,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.listUserOrders(userId, page, size));
    }

    @GetMapping("/{orderId}")
    public Result<Map<String, Object>> detail(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        Orders order = orderService.getOrderDetail(userId, orderId);
        List<OrderItem> items = orderService.getOrderItems(orderId);
        return Result.success(Map.of("order", order, "items", items));
    }

    @PostMapping("/{orderId}/pay")
    public Result<Void> pay(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(userId, orderId);
        return Result.success();
    }

    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancel(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }

    @PostMapping("/{orderId}/complete")
    public Result<Void> complete(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.completeOrder(userId, orderId);
        return Result.success();
    }
}
