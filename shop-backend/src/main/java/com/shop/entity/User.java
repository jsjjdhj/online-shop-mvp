package com.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String role;             // USER / ADMIN

    private Integer status;          // 0-正常, 1-锁定

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime lockTime;  // 锁定到期时间

    private Integer failCount;       // 连续登录失败次数

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
