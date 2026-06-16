package com.shop.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartRepository extends BaseMapper<Cart> {

    default List<Cart> findByUserId(Long userId) {
        return this.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
        );
    }

    default Optional<Cart> findByUserIdAndProductId(Long userId, Long productId) {
        return Optional.ofNullable(this.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getProductId, productId)
        ));
    }
}
