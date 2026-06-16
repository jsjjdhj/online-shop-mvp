package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.BusinessException;
import com.shop.common.PageResult;
import com.shop.dto.OrderSubmitRequest;
import com.shop.entity.*;
import com.shop.repository.*;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    private final AtomicLong orderSeq = new AtomicLong(0);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders submitOrder(Long userId, OrderSubmitRequest request) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车中没有商品，无法结算");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            Product product = productRepository.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 0) {
                throw new BusinessException("商品[" + cart.getProductId() + "]已下架");
            }
            if (cart.getQuantity() > product.getStock()) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足，当前库存为" + product.getStock());
            }
        }

        // 生成订单号
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", orderSeq.incrementAndGet() % 10000);

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(0);
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientAddress(request.getRecipientAddress());
        ordersRepository.insert(order);

        for (Cart cart : cartItems) {
            Product product = productRepository.selectById(cart.getProductId());
            int affected = productRepository.deductStock(product.getId(), cart.getQuantity());
            if (affected == 0) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足，当前库存为" + product.getStock());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(subtotal);
            orderItemRepository.insert(item);
        }

        order.setTotalAmount(totalAmount);
        ordersRepository.updateById(order);

        cartRepository.deleteBatchIds(cartItems.stream().map(Cart::getId).toList());

        return order;
    }

    @Override
    public PageResult<Orders> listUserOrders(Long userId, int page, int size) {
        IPage<Orders> pageResult = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreatedAt);
        ordersRepository.selectPage(pageResult, wrapper);
        return new PageResult<>(pageResult.getTotal(), page, size, pageResult.getRecords());
    }

    @Override
    public Orders getOrderDetail(Long userId, Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    @Override
    public void payOrder(Long userId, Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，无法付款");
        }
        order.setStatus(2);
        order.setPaidAt(LocalDateTime.now());
        ordersRepository.updateById(order);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() > 1) {
            throw new BusinessException("已付款订单无法取消，请联系客服");
        }
        order.setStatus(5);
        order.setCancelledAt(LocalDateTime.now());
        ordersRepository.updateById(order);
        restoreStock(order);
    }

    @Override
    public void confirmOrder(Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 0) throw new BusinessException("订单状态不正确");
        order.setStatus(1);
        ordersRepository.updateById(order);
    }

    @Override
    public void shipOrder(Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("订单未付款，无法发货");
        order.setStatus(3);
        order.setShippedAt(LocalDateTime.now());
        ordersRepository.updateById(order);
        order.setStatus(4);
        order.setCompletedAt(LocalDateTime.now());
        ordersRepository.updateById(order);
    }

    private void restoreStock(Orders order) {
        List<OrderItem> items = orderItemRepository.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        for (OrderItem item : items) {
            Product product = productRepository.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.updateById(product);
            }
        }
    }
}
