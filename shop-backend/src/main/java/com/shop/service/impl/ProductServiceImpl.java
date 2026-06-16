package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.BusinessException;
import com.shop.common.PageResult;
import com.shop.dto.ProductRequest;
import com.shop.entity.Product;
import com.shop.repository.ProductRepository;
import com.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResult<Product> listProducts(int page, int size, String keyword) {
        IPage<Product> pageResult = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 0);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Product::getName, kw)
                    .or()
                    .like(Product::getDescription, kw));
        }
        wrapper.orderByDesc(Product::getCreatedAt);
        productRepository.selectPage(pageResult, wrapper);
        return new PageResult<>(
                pageResult.getTotal(),
                page, size,
                pageResult.getRecords()
        );
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public void createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(0);
        productRepository.insert(product);
    }

    @Override
    public void updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        productRepository.updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(1); // 下架
        productRepository.updateById(product);
    }
}
