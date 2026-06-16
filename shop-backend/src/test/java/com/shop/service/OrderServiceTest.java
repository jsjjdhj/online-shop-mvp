package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
import com.shop.common.PageResult;
import com.shop.dto.*;
import com.shop.entity.*;
import com.shop.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private CartService cartService;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    private static final String PWD = "Test1234";

    private Long createUser(String s) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("otest_" + s + "_" + System.nanoTime());
        r.setPassword(PWD); r.setConfirmPassword(PWD);
        userService.register(r);
        return userRepository.findByUsername(r.getUsername()).get().getId();
    }

    private Long createProd(String name) {
        ProductRequest r = new ProductRequest();
        r.setName(name); r.setDescription("desc");
        r.setPrice(new BigDecimal("199.00")); r.setStock(50);
        productService.createProduct(r);
        var result = productService.listProducts(1, 100, name);
        return result.getRecords().stream().filter(p -> p.getName().equals(name)).findFirst().get().getId();
    }

    private Long submitOrder(Long uid, Long pid) {
        CartRequest c = new CartRequest();
        c.setProductId(pid); c.setQuantity(2);
        cartService.addItem(uid, c);
        OrderSubmitRequest o = new OrderSubmitRequest();
        o.setRecipientName("Zhang"); o.setRecipientPhone("13800138000"); o.setRecipientAddress("Beijing");
        return orderService.submitOrder(uid, o).getId();
    }

    @Test @Order(1) void testSubmit_Success() {
        Long uid = createUser("su"); Long pid = createProd("OSubmit");
        Long oid = submitOrder(uid, pid);
        assertNotNull(oid);
        Orders o = ordersRepository.selectById(oid);
        assertEquals(0, o.getStatus().intValue());
        assertNotNull(o.getOrderNo());
    }

    @Test @Order(2) void testSubmit_EmptyCart() {
        Long uid = createUser("ec");
        OrderSubmitRequest r = new OrderSubmitRequest();
        r.setRecipientName("Li"); r.setRecipientPhone("13900139000"); r.setRecipientAddress("SH");
        assertThrows(BusinessException.class, () -> orderService.submitOrder(uid, r));
    }

    @Test @Order(3) void testFullFlow() {
        Long uid = createUser("ff"); Long pid = createProd("FFlow");
        Long oid = submitOrder(uid, pid);
        assertEquals(0, ordersRepository.selectById(oid).getStatus().intValue());
        orderService.confirmOrder(oid);
        assertEquals(1, ordersRepository.selectById(oid).getStatus().intValue());
        orderService.payOrder(uid, oid);
        assertEquals(2, ordersRepository.selectById(oid).getStatus().intValue());
        orderService.shipOrder(oid);
        assertEquals(3, ordersRepository.selectById(oid).getStatus().intValue());
        orderService.completeOrder(uid, oid);
        assertEquals(4, ordersRepository.selectById(oid).getStatus().intValue());
    }

    @Test @Order(4) void testCancel_RestoreStock() {
        Long uid = createUser("cl"); Long pid = createProd("Cancel");
        Product b4 = productRepository.selectById(pid);
        int sb = b4.getStock();
        Long oid = submitOrder(uid, pid);
        orderService.cancelOrder(uid, oid);
        assertEquals(5, ordersRepository.selectById(oid).getStatus().intValue());
        Product aft = productRepository.selectById(pid);
        assertEquals(sb, aft.getStock().intValue());
    }

    @Test @Order(5) void testListAllOrders() {
        Long uid = createUser("la"); Long pid = createProd("ListAll");
        submitOrder(uid, pid);
        PageResult<Orders> r = orderService.listAllOrders(1, 10, null);
        assertTrue(r.getTotal() > 0);
    }

    @Test @Order(6) void testGetOrderItems() {
        Long uid = createUser("it"); Long pid = createProd("Items");
        Long oid = submitOrder(uid, pid);
        var items = orderService.getOrderItems(oid);
        assertTrue(items.size() > 0);
        assertEquals(pid, items.get(0).getProductId());
    }
}
