package com.shop.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {

    IPage<Product> searchProducts(IPage<Product> page,
                                  @Param("keyword") String keyword);

    @Update("UPDATE product SET stock = stock - #{quantity} " +
            "WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId,
                    @Param("quantity") int quantity);
}
