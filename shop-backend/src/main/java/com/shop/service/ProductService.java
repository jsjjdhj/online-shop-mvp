package com.shop.service;

import com.shop.common.PageResult;
import com.shop.dto.ProductRequest;
import com.shop.entity.Product;

public interface ProductService {

    PageResult<Product> listProducts(int page, int size, String keyword);

    Product getProductById(Long id);

    void createProduct(ProductRequest request);

    void updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
