package com.shop.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository extends BaseMapper<OrderItem> {
}
