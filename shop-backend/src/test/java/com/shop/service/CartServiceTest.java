package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
import com.shop.dto.CartRequest;
import com.shop.dto.CartUpdateRequest;
import com.shop.dto.RegisterRequest;
import com.shop.entity.Cart;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.repository.CartRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
class CartServiceTest {

    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        String username = "cart_test_" + System.nanoTime();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("Test1234");
        registerRequest.setConfirmPassword("Test1234");
        userService.register(registerRequest);

        // 获取用户ID
        User user = userRepository.findByUsername(username).orElseThrow();
        userId = user.getId();

        // 创建测试商品（库存100）
        Product product = new Product();
        product.setName("测试商品");
        product.setDescription("测试描述");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(100);
        product.setStatus(0);
        productRepository.insert(product);
        productId = product.getId();
    }

    // ========== 添加商品到购物车测试 ==========

    @Test
    void testAddItem_Success_Quantity1() {
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        
        assertDoesNotThrow(() -> cartService.addItem(userId, request));
        
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(1, carts.size());
        assertEquals(1, carts.get(0).getQuantity());
    }

    @Test
    void testAddItem_Success_Quantity99() {
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(99);
        
        assertDoesNotThrow(() -> cartService.addItem(userId, request));
        
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(1, carts.size());
        assertEquals(99, carts.get(0).getQuantity());
    }

    @Test
    void testAddItem_Fail_Quantity0() {
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(0);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request));
        assertTrue(e.getMessage().contains("不能小于1"));
    }

    @Test
    void testAddItem_Fail_Quantity100() {
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(100);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request));
        assertTrue(e.getMessage().contains("不能超过99"));
    }

    @Test
    void testAddItem_Fail_ExceedsStock() {
        // 创建库存为5的商品
        Product limitedProduct = new Product();
        limitedProduct.setName("限量商品");
        limitedProduct.setDescription("库存5件");
        limitedProduct.setPrice(BigDecimal.valueOf(50.00));
        limitedProduct.setStock(5);
        limitedProduct.setStatus(0);
        productRepository.insert(limitedProduct);

        CartRequest request = new CartRequest();
        request.setProductId(limitedProduct.getId());
        request.setQuantity(10);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request));
        assertTrue(e.getMessage().contains("库存不足"));
    }

    @Test
    void testAddItem_Success_DuplicateProductAccumulates() {
        // 第一次添加5件
        CartRequest request1 = new CartRequest();
        request1.setProductId(productId);
        request1.setQuantity(5);
        cartService.addItem(userId, request1);

        // 第二次添加3件（重复加购）
        CartRequest request2 = new CartRequest();
        request2.setProductId(productId);
        request2.setQuantity(3);
        cartService.addItem(userId, request2);

        // 验证数量累加
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(1, carts.size()); // 只有一条记录
        assertEquals(8, carts.get(0).getQuantity()); // 5+3=8
    }

    @Test
    void testAddItem_Fail_DuplicateExceedsMax() {
        // 第一次添加95件
        CartRequest request1 = new CartRequest();
        request1.setProductId(productId);
        request1.setQuantity(95);
        cartService.addItem(userId, request1);

        // 第二次添加10件，累加后105超过99
        CartRequest request2 = new CartRequest();
        request2.setProductId(productId);
        request2.setQuantity(10);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request2));
        assertTrue(e.getMessage().contains("不能超过99"));
    }

    @Test
    void testAddItem_Fail_DuplicateExceedsStock() {
        // 创建库存为10的商品
        Product limitedProduct = new Product();
        limitedProduct.setName("库存10商品");
        limitedProduct.setDescription("库存10件");
        limitedProduct.setPrice(BigDecimal.valueOf(30.00));
        limitedProduct.setStock(10);
        limitedProduct.setStatus(0);
        productRepository.insert(limitedProduct);

        // 第一次添加6件
        CartRequest request1 = new CartRequest();
        request1.setProductId(limitedProduct.getId());
        request1.setQuantity(6);
        cartService.addItem(userId, request1);

        // 第二次添加5件，累加后11超过库存10
        CartRequest request2 = new CartRequest();
        request2.setProductId(limitedProduct.getId());
        request2.setQuantity(5);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request2));
        assertTrue(e.getMessage().contains("库存不足"));
    }

    @Test
    void testAddItem_Fail_ProductNotExist() {
        CartRequest request = new CartRequest();
        request.setProductId(999999L); // 不存在的商品ID
        request.setQuantity(1);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.addItem(userId, request));
        assertTrue(e.getMessage().contains("商品不存在或已下架"));
    }

    // ========== 更新购物车数量测试 ==========

    @Test
    void testUpdateQuantity_Success() {
        // 先添加商品
        CartRequest addRequest = new CartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(5);
        cartService.addItem(userId, addRequest);

        Cart cart = cartService.listCartItems(userId).get(0);
        
        // 更新数量为10
        CartUpdateRequest updateRequest = new CartUpdateRequest();
        updateRequest.setQuantity(10);
        cartService.updateQuantity(userId, cart.getId(), updateRequest);

        // 验证更新结果
        Cart updatedCart = cartRepository.selectById(cart.getId());
        assertEquals(10, updatedCart.getQuantity());
    }

    @Test
    void testUpdateQuantity_Success_SetToZeroDeletes() {
        // 先添加商品
        CartRequest addRequest = new CartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(5);
        cartService.addItem(userId, addRequest);

        Cart cart = cartService.listCartItems(userId).get(0);
        
        // 更新数量为0（删除）
        CartUpdateRequest updateRequest = new CartUpdateRequest();
        updateRequest.setQuantity(0);
        cartService.updateQuantity(userId, cart.getId(), updateRequest);

        // 验证记录已删除
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(0, carts.size());
    }

    @Test
    void testUpdateQuantity_Fail_ExceedsStock() {
        // 创建库存为5的商品
        Product limitedProduct = new Product();
        limitedProduct.setName("库存5商品");
        limitedProduct.setDescription("库存5件");
        limitedProduct.setPrice(BigDecimal.valueOf(20.00));
        limitedProduct.setStock(5);
        limitedProduct.setStatus(0);
        productRepository.insert(limitedProduct);

        // 先添加3件
        CartRequest addRequest = new CartRequest();
        addRequest.setProductId(limitedProduct.getId());
        addRequest.setQuantity(3);
        cartService.addItem(userId, addRequest);

        Cart cart = cartService.listCartItems(userId).get(0);
        
        // 更新数量为10（超过库存5）
        CartUpdateRequest updateRequest = new CartUpdateRequest();
        updateRequest.setQuantity(10);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.updateQuantity(userId, cart.getId(), updateRequest));
        assertTrue(e.getMessage().contains("库存不足"));
    }

    @Test
    void testUpdateQuantity_Fail_InvalidCartId() {
        CartUpdateRequest updateRequest = new CartUpdateRequest();
        updateRequest.setQuantity(5);
        
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.updateQuantity(userId, 999999L, updateRequest));
        assertTrue(e.getMessage().contains("购物车记录不存在"));
    }

    // ========== 删除购物车商品测试 ==========

    @Test
    void testRemoveItem_Success() {
        // 先添加商品
        CartRequest addRequest = new CartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(5);
        cartService.addItem(userId, addRequest);

        Cart cart = cartService.listCartItems(userId).get(0);
        
        // 删除
        assertDoesNotThrow(() -> cartService.removeItem(userId, cart.getId()));

        // 验证已删除
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(0, carts.size());
    }

    @Test
    void testRemoveItem_Fail_InvalidCartId() {
        BusinessException e = assertThrows(BusinessException.class, 
                () -> cartService.removeItem(userId, 999999L));
        assertTrue(e.getMessage().contains("购物车记录不存在"));
    }

    // ========== 列出购物车商品测试 ==========

    @Test
    void testListCartItems_EmptyCart() {
        List<Cart> carts = cartService.listCartItems(userId);
        assertNotNull(carts);
        assertEquals(0, carts.size());
    }

    @Test
    void testListCartItems_MultipleItems() {
        // 添加第一个商品
        CartRequest request1 = new CartRequest();
        request1.setProductId(productId);
        request1.setQuantity(2);
        cartService.addItem(userId, request1);

        // 创建第二个商品
        Product product2 = new Product();
        product2.setName("测试商品2");
        product2.setDescription("测试描述2");
        product2.setPrice(BigDecimal.valueOf(50.00));
        product2.setStock(50);
        product2.setStatus(0);
        productRepository.insert(product2);

        // 添加第二个商品
        CartRequest request2 = new CartRequest();
        request2.setProductId(product2.getId());
        request2.setQuantity(3);
        cartService.addItem(userId, request2);

        // 验证列表
        List<Cart> carts = cartService.listCartItems(userId);
        assertEquals(2, carts.size());
    }
}
