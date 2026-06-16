package com.shop.service;

import com.shop.common.PageResult;
import com.shop.dto.OrderSubmitRequest;
import com.shop.entity.Orders;

public interface OrderService {

    Orders submitOrder(Long userId, OrderSubmitRequest request);

    PageResult<Orders> listUserOrders(Long userId, int page, int size);

    Orders getOrderDetail(Long userId, Long orderId);

    void payOrder(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    void confirmOrder(Long orderId);

    void shipOrder(Long orderId);
}
