package com.shop.service.impl;

import com.shop.common.BusinessException;
import com.shop.dto.CartRequest;
import com.shop.dto.CartUpdateRequest;
import com.shop.entity.Cart;
import com.shop.entity.Product;
import com.shop.repository.CartRepository;
import com.shop.repository.ProductRepository;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public void addItem(Long userId, CartRequest request) {
        if (request.getQuantity() < 1) {
            throw new BusinessException("添加数量不能小于1");
        }
        if (request.getQuantity() > 99) {
            throw new BusinessException("添加数量不能超过99");
        }
        Product product = productRepository.selectById(request.getProductId());
        if (product == null || product.getDeleted() == 1 || product.getStatus() != 0) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (request.getQuantity() > product.getStock()) {
            throw new BusinessException("商品库存不足，当前库存为" + product.getStock());
        }

        Optional<Cart> existing = cartRepository.findByUserIdAndProductId(userId, request.getProductId());
        if (existing.isPresent()) {
            Cart cart = existing.get();
            int newQty = cart.getQuantity() + request.getQuantity();
            if (newQty > 99) {
                throw new BusinessException("购物车中该商品数量不能超过99");
            }
            if (newQty > product.getStock()) {
                throw new BusinessException("商品库存不足，当前库存为" + product.getStock());
            }
            cart.setQuantity(newQty);
            cartRepository.updateById(cart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
            cartRepository.insert(cart);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long cartId, CartUpdateRequest request) {
        Cart cart = cartRepository.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }
        if (request.getQuantity() == 0) {
            cartRepository.deleteById(cartId);
            return;
        }
        Product product = productRepository.selectById(cart.getProductId());
        if (product != null && request.getQuantity() > product.getStock()) {
            throw new BusinessException("商品库存不足，当前库存为" + product.getStock());
        }
        cart.setQuantity(request.getQuantity());
        cartRepository.updateById(cart);
    }

    @Override
    public void removeItem(Long userId, Long cartId) {
        Cart cart = cartRepository.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }
        cartRepository.deleteById(cartId);
    }

    @Override
    public List<Cart> listCartItems(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
