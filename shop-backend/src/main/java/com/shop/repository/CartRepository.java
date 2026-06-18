package com.shop.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /** 查找包含已软删除的记录（使用原生SQL绕过@TableLogic过滤） */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    Optional<Cart> findIncludingDeleted(Long userId, Long productId);

    /** 恢复软删除的购物车记录并更新数量（使用原生SQL绕过@TableLogic过滤） */
    @Update("UPDATE cart SET quantity = #{quantity}, deleted = 0 WHERE id = #{id}")
    int restoreDeleted(@Param("id") Long id, @Param("quantity") int quantity);
}
