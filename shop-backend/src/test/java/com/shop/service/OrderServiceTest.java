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
        r.setUsername("ot_" + s + "_" + System.nanoTime());
        r.setPassword(PWD); r.setConfirmPassword(PWD);
        userService.register(r);
        return userRepository.findByUsername(r.getUsername()).get().getId();
    }

    private Long createProdWithSvc(String name) {
        ProductRequest r = new ProductRequest();
        r.setName(name); r.setDescription("desc");
        r.setPrice(new BigDecimal("199.00")); r.setStock(50);
        productService.createProduct(r);
        PageResult<Product> result = productService.listProducts(1, 100, name);
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

    private Long createOrderInState(Long uid, Long pid, int targetStatus) {
        Long oid = submitOrder(uid, pid);
        if (targetStatus == 0) return oid;
        orderService.confirmOrder(oid);
        if (targetStatus == 1) return oid;
        orderService.payOrder(uid, oid);
        if (targetStatus == 2) return oid;
        orderService.shipOrder(oid);
        if (targetStatus == 3) return oid;
        orderService.completeOrder(uid, oid);
        return oid;
    }

    // ===== OT-1 ~ OT-6: existing tests =====

    @Test @Order(1)
    void testSubmit_Success() {
        Long uid = createUser("su"); Long pid = createProdWithSvc("OSubmit");
        Long oid = submitOrder(uid, pid);
        assertNotNull(oid);
        Orders o = ordersRepository.selectById(oid);
        assertEquals(0, o.getStatus().intValue());
        assertNotNull(o.getOrderNo());
    }

    @Test @Order(2)
    void testSubmit_EmptyCart() {
        Long uid = createUser("ec");
        OrderSubmitRequest r = new OrderSubmitRequest();
        r.setRecipientName("Li"); r.setRecipientPhone("13900139000"); r.setRecipientAddress("SH");
        assertThrows(BusinessException.class, () -> orderService.submitOrder(uid, r));
    }

    @Test @Order(3)
    void testFullFlow() {
        Long uid = createUser("ff"); Long pid = createProdWithSvc("FFlow");
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

    @Test @Order(4)
    void testCancel_RestoreStock() {
        Long uid = createUser("cl"); Long pid = createProdWithSvc("Cancel");
        Product b4 = productRepository.selectById(pid);
        int sb = b4.getStock();
        Long oid = submitOrder(uid, pid);
        orderService.cancelOrder(uid, oid);
        assertEquals(5, ordersRepository.selectById(oid).getStatus().intValue());
        Product aft = productRepository.selectById(pid);
        assertEquals(sb, aft.getStock().intValue());
    }

    @Test @Order(5)
    void testListAllOrders() {
        Long uid = createUser("la"); Long pid = createProdWithSvc("ListAll");
        submitOrder(uid, pid);
        PageResult<Orders> r = orderService.listAllOrders(1, 10, null);
        assertTrue(r.getTotal() > 0);
    }

    @Test @Order(6)
    void testGetOrderItems() {
        Long uid = createUser("it"); Long pid = createProdWithSvc("Items");
        Long oid = submitOrder(uid, pid);
        var items = orderService.getOrderItems(oid);
        assertTrue(items.size() > 0);
        assertEquals(pid, items.get(0).getProductId());
    }

    // ===== P1: OT-7 ~ OT-13 (exception paths) =====

    @Test @Order(7)
    void testPayOrder_WrongStatus() {
        Long uid = createUser("pw"); Long pid = createProdWithSvc("PayWr");
        Long oid = createOrderInState(uid, pid, 3); // shipped
        assertThrows(BusinessException.class, () -> orderService.payOrder(uid, oid));
    }

    @Test @Order(8)
    void testCancelOrder_AlreadyPaid() {
        Long uid = createUser("cp"); Long pid = createProdWithSvc("CanPaid");
        Long oid = createOrderInState(uid, pid, 2); // paid
        BusinessException e = assertThrows(BusinessException.class, () -> orderService.cancelOrder(uid, oid));
        assertTrue(e.getMessage().contains("无法取消"));
    }

    @Test @Order(9)
    void testCompleteOrder_NotShipped() {
        Long uid = createUser("co"); Long pid = createProdWithSvc("ComNS");
        Long oid = createOrderInState(uid, pid, 2); // paid
        BusinessException e = assertThrows(BusinessException.class, () -> orderService.completeOrder(uid, oid));
        assertTrue(e.getMessage().contains("未发货"));
    }

    @Test @Order(10)
    void testConfirmOrder_NotPending() {
        Long uid = createUser("cn"); Long pid = createProdWithSvc("ConNP");
        Long oid = createOrderInState(uid, pid, 1); // confirmed
        assertThrows(BusinessException.class, () -> orderService.confirmOrder(oid));
    }

    @Test @Order(11)
    void testShipOrder_NotPaid() {
        Long uid = createUser("sh"); Long pid = createProdWithSvc("ShNP");
        Long oid = createOrderInState(uid, pid, 1); // confirmed
        BusinessException e = assertThrows(BusinessException.class, () -> orderService.shipOrder(oid));
        assertTrue(e.getMessage().contains("未付款"));
    }



    @Test @Order(12)
    void testSubmitOrder_ProductOffline() {
        Long uid = createUser("po"); Long pid = createProdWithSvc("Offline");
        CartRequest c = new CartRequest();
        c.setProductId(pid); c.setQuantity(1);
        cartService.addItem(uid, c);
        productService.deleteProduct(pid);
        OrderSubmitRequest o = new OrderSubmitRequest();
        o.setRecipientName("X"); o.setRecipientPhone("13800138000"); o.setRecipientAddress("X");
        assertThrows(BusinessException.class, () -> orderService.submitOrder(uid, o));
    }
    @Test @Order(13)
    void testSubmitOrder_InsufficientStock() {
        Long uid = createUser("is"); Long pid = createProdWithSvc("Stock");
        Product p = productRepository.selectById(pid);
        p.setStock(1);
        productRepository.updateById(p);
        CartRequest c = new CartRequest();
        c.setProductId(pid); c.setQuantity(5); // > stock
        assertThrows(BusinessException.class, () -> cartService.addItem(uid, c));
    }

    // ===== P2: OT-14 ~ OT-17 (functional completeness) =====

    @Test @Order(14)
    void testListUserOrders_OwnOnly() {
        Long uid1 = createUser("lo1"); Long pid = createProdWithSvc("ListOwn");
        submitOrder(uid1, pid);
        Long uid2 = createUser("lo2");
        PageResult<Orders> r = orderService.listUserOrders(uid2, 1, 10);
        assertEquals(0, r.getTotal());
    }

    @Test @Order(15)
    void testGetOrderDetail_WrongUser() {
        Long uid1 = createUser("wd1"); Long pid = createProdWithSvc("WrUsr");
        Long oid = submitOrder(uid1, pid);
        Long uid2 = createUser("wd2");
        assertThrows(BusinessException.class, () -> orderService.getOrderDetail(uid2, oid));
    }

    @Test @Order(16)
    void testGetAdminOrderDetail_Success() {
        Long uid = createUser("ad"); Long pid = createProdWithSvc("AdmDtl");
        Long oid = submitOrder(uid, pid);
        Orders o = orderService.getAdminOrderDetail(oid);
        assertNotNull(o);
        assertEquals(oid, o.getId());
    }

    @Test @Order(17)
    void testListAllOrders_ByStatus() {
        Long uid = createUser("lbs"); Long pid = createProdWithSvc("ListSt");
        submitOrder(uid, pid);
        PageResult<Orders> r = orderService.listAllOrders(1, 10, 0);
        assertTrue(r.getTotal() > 0);
        r.getRecords().forEach(o -> assertEquals(0, o.getStatus().intValue()));
    }

}
