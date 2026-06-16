package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
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
class CartServiceTest {

    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    private static final String PWD = "Test1234";

    private Long createUser(String s) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("ct_" + s + "_" + System.nanoTime());
        r.setPassword(PWD); r.setConfirmPassword(PWD);
        userService.register(r);
        return userRepository.findByUsername(r.getUsername()).get().getId();
    }

    private Long createProd(String name, int stock) {
        ProductRequest r = new ProductRequest();
        r.setName(name); r.setDescription("desc");
        r.setPrice(new BigDecimal("99.00")); r.setStock(stock);
        productService.createProduct(r);
        var result = productService.listProducts(1, 100, name);
        return result.getRecords().stream().filter(p -> p.getName().equals(name)).findFirst().get().getId();
    }

    private Long addToCart(Long uid, Long pid, int qty) {
        CartRequest c = new CartRequest();
        c.setProductId(pid); c.setQuantity(qty);
        cartService.addItem(uid, c);
        return cartRepository.findByUserId(uid).get(0).getId();
    }

    @Test void testAddItem_Success() {
        Long uid = createUser("a1"); Long pid = createProd("CatAdd1", 50);
        addToCart(uid, pid, 3);
        var items = cartRepository.findByUserId(uid);
        assertEquals(1, items.size()); assertEquals(3, items.get(0).getQuantity().intValue());
    }

    @Test void testAddItem_DuplicateMerge() {
        Long uid = createUser("a2"); Long pid = createProd("CatDup", 50);
        addToCart(uid, pid, 2); addToCart(uid, pid, 3);
        var items = cartRepository.findByUserId(uid);
        assertEquals(1, items.size()); assertEquals(5, items.get(0).getQuantity().intValue());
    }

    @Test void testAddItem_ProductOffline() {
        Long uid = createUser("a3"); Long pid = createProd("CatOff", 50);
        var p = productRepository.selectById(pid);
        p.setStatus(1); productRepository.updateById(p);
        CartRequest c = new CartRequest(); c.setProductId(pid); c.setQuantity(1);
        assertThrows(BusinessException.class, () -> cartService.addItem(uid, c));
    }

    @Test void testAddItem_InsufficientStock() {
        Long uid = createUser("a4"); Long pid = createProd("CatStk", 3);
        CartRequest c = new CartRequest(); c.setProductId(pid); c.setQuantity(10);
        assertThrows(BusinessException.class, () -> cartService.addItem(uid, c));
    }

    @Test void testAddItem_ExceedMax99OnMerge() {
        Long uid = createUser("a5"); Long pid = createProd("CatMax", 100);
        addToCart(uid, pid, 50);
        CartRequest c = new CartRequest(); c.setProductId(pid); c.setQuantity(50);
        var e = assertThrows(BusinessException.class, () -> cartService.addItem(uid, c));
        assertTrue(e.getMessage().contains("99"));
    }

    @Test void testUpdateQuantity_Success() {
        Long uid = createUser("a6"); Long pid = createProd("CatUpd", 50);
        Long cid = addToCart(uid, pid, 2);
        CartUpdateRequest u = new CartUpdateRequest(); u.setQuantity(8);
        cartService.updateQuantity(uid, cid, u);
        assertEquals(8, cartRepository.selectById(cid).getQuantity().intValue());
    }

    @Test void testUpdateQuantity_DeleteWhenZero() {
        Long uid = createUser("a7"); Long pid = createProd("CatDel", 50);
        Long cid = addToCart(uid, pid, 2);
        CartUpdateRequest u = new CartUpdateRequest(); u.setQuantity(0);
        cartService.updateQuantity(uid, cid, u);
        assertNull(cartRepository.selectById(cid));
    }

    @Test void testUpdateQuantity_OverStock() {
        Long uid = createUser("a8"); Long pid = createProd("CatOver", 5);
        Long cid = addToCart(uid, pid, 2);
        CartUpdateRequest u = new CartUpdateRequest(); u.setQuantity(10);
        assertThrows(BusinessException.class, () -> cartService.updateQuantity(uid, cid, u));
    }

    @Test void testRemoveItem_Success() {
        Long uid = createUser("a9"); Long pid = createProd("CatRem", 50);
        Long cid = addToCart(uid, pid, 2);
        cartService.removeItem(uid, cid);
        assertTrue(cartRepository.findByUserId(uid).isEmpty());
    }

    @Test void testRemoveItem_NotOwner() {
        Long uidA = createUser("r1"); Long pid = createProd("CatOwn", 50);
        Long cid = addToCart(uidA, pid, 2);
        Long uidB = createUser("r2");
        assertThrows(BusinessException.class, () -> cartService.removeItem(uidB, cid));
    }

    @Test void testListCartItems_Success() {
        Long uid = createUser("a11");
        Long p1 = createProd("CatLst1", 50); Long p2 = createProd("CatLst2", 30);
        addToCart(uid, p1, 2); addToCart(uid, p2, 1);
        var items = cartService.listCartItems(uid);
        assertEquals(2, items.size());
    }
}
