package com.shop.service;

import com.shop.ShopApplication;
import com.shop.common.BusinessException;
import com.shop.common.PageResult;
import com.shop.dto.ProductRequest;
import com.shop.entity.Product;
import com.shop.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
class ProductServiceTest {

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;

    private Long createProd(String name) {
        ProductRequest r = new ProductRequest();
        r.setName(name); r.setDescription("desc");
        r.setPrice(new BigDecimal("99.00")); r.setStock(10);
        productService.createProduct(r);
        PageResult<Product> result = productService.listProducts(1, 100, name);
        return result.getRecords().stream().filter(p -> p.getName().equals(name)).findFirst().get().getId();
    }

    @Test
    void testCreateProduct_Success() {
        String name = "pdt_" + System.nanoTime();
        Long id = createProd(name);
        Product p = productService.getProductById(id);
        assertEquals(name, p.getName());
        assertEquals(0, p.getStatus().intValue());
        assertEquals(0, new BigDecimal("99.00").compareTo(p.getPrice()));
        assertEquals(10, p.getStock().intValue());
    }

    @Test
    void testUpdateProduct_Success() {
        Long id = createProd("upd_" + System.nanoTime());
        ProductRequest r = new ProductRequest();
        r.setName("updated_name"); r.setDescription("new desc");
        r.setPrice(new BigDecimal("199.00")); r.setStock(50);
        productService.updateProduct(id, r);
        Product p = productService.getProductById(id);
        assertEquals("updated_name", p.getName());
        assertEquals(0, new BigDecimal("199.00").compareTo(p.getPrice()));
        assertEquals(50, p.getStock().intValue());
    }

    @Test
    void testDeleteProduct_Success() {
        Long id = createProd("del_" + System.nanoTime());
        productService.deleteProduct(id);
        Product p = productService.getProductById(id);
        assertEquals(1, p.getStatus().intValue());
    }

    @Test
    void testUpdateProduct_NotFound() {
        ProductRequest r = new ProductRequest();
        r.setName("x"); r.setPrice(new BigDecimal("1")); r.setStock(1);
        assertThrows(BusinessException.class, () -> productService.updateProduct(99999L, r));
    }

    @Test
    void testDeleteProduct_NotFound() {
        assertThrows(BusinessException.class, () -> productService.deleteProduct(99999L));
    }

    @Test
    void testGetProductById_NotFound() {
        assertThrows(BusinessException.class, () -> productService.getProductById(99999L));
    }

    @Test
    void testGetProductById_DeletedLogical() {
        Long id = createProd("del2_" + System.nanoTime());
        productService.deleteProduct(id);
        // deleteProduct sets status=1, not deleted=1, so getProductById should still return it
        Product p = productService.getProductById(id);
        assertEquals(1, p.getStatus().intValue());
    }

    @Test
    void testListProducts_KeywordSearch() {
        String kw = "kws_" + System.nanoTime();
        createProd(kw);
        PageResult<Product> result = productService.listProducts(1, 10, kw);
        assertTrue(result.getTotal() > 0);
        assertTrue(result.getRecords().stream().anyMatch(p -> p.getName().equals(kw)));
    }

    @Test
    void testListProducts_Pagination() {
        for (int i = 0; i < 3; i++) {
            createProd("pg_" + i + "_" + System.nanoTime());
        }
        PageResult<Product> p1 = productService.listProducts(1, 2, null);
        assertTrue(p1.getRecords().size() <= 2);
        assertTrue(p1.getTotal() >= 1);
    }
}
