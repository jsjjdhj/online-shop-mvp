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

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_CONFIRMED = 1;
    private static final int STATUS_PAID = 2;
    private static final int STATUS_SHIPPED = 3;
    private static final int STATUS_COMPLETED = 4;
    private static final int STATUS_CANCELLED = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders submitOrder(Long userId, OrderSubmitRequest request) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车中没有商品，无法结算");
        }
        for (Cart cart : cartItems) {
            Product product = productRepository.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 0) {
                throw new BusinessException("商品[" + cart.getProductId() + "]已下架");
            }
            if (cart.getQuantity() > product.getStock()) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足，当前库存为" + product.getStock());
            }
        }
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", orderSeq.incrementAndGet() % 10000);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            Product product = productRepository.selectById(cart.getProductId());
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(STATUS_PENDING);
        order.setTotalAmount(totalAmount);
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientAddress(request.getRecipientAddress());
        ordersRepository.insert(order);
        for (Cart cart : cartItems) {
            Product product = productRepository.selectById(cart.getProductId());
            int affected = productRepository.deductStock(product.getId(), cart.getQuantity());
            if (affected == 0) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemRepository.insert(item);
        }
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
        if (order.getStatus() != STATUS_CONFIRMED && order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("订单状态不正确，无法付款（当前状态：" + order.getStatus() + "）");
        }
        order.setStatus(STATUS_PAID);
        order.setPaidAt(LocalDateTime.now());
        ordersRepository.updateById(order);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() > STATUS_CONFIRMED) {
            throw new BusinessException("已付款订单无法取消，请联系客服");
        }
        order.setStatus(STATUS_CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        ordersRepository.updateById(order);
        restoreStock(order);
    }

    @Override
    public void completeOrder(Long userId, Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != STATUS_SHIPPED) {
            throw new BusinessException("订单未发货，无法确认收货");
        }
        order.setStatus(STATUS_COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        ordersRepository.updateById(order);
    }

    @Override
    public void confirmOrder(Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("订单状态不正确，当前状态：" + order.getStatus());
        }
        order.setStatus(STATUS_CONFIRMED);
        ordersRepository.updateById(order);
    }

    @Override
    public void shipOrder(Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != STATUS_PAID) {
            throw new BusinessException("订单未付款，无法发货");
        }
        order.setStatus(STATUS_SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        ordersRepository.updateById(order);
    }

    @Override
    public PageResult<Orders> listAllOrders(int page, int size, Integer status) {
        IPage<Orders> pageResult = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .orderByDesc(Orders::getCreatedAt);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        ordersRepository.selectPage(pageResult, wrapper);
        return new PageResult<>(pageResult.getTotal(), page, size, pageResult.getRecords());
    }

    @Override
    public Orders getAdminOrderDetail(Long orderId) {
        Orders order = ordersRepository.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        return order;
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
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
