package com.shop.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRepository extends BaseMapper<User> {

    default Optional<User> findByUsername(String username) {
        return Optional.ofNullable(this.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        ));
    }
}
