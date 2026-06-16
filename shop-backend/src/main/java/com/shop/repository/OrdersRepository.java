package com.shop.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersRepository extends BaseMapper<Orders> {
}
