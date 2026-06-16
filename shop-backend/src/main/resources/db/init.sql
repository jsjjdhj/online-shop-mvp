-- ============================================================
-- 在线购物平台 MVP - 数据库初始化脚本
-- 软件质量与测试课 2025-2026-2 学期
-- ============================================================

CREATE DATABASE IF NOT EXISTS shop_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_dev;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    `status`      INT          NOT NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-锁定',
    `lock_time`   DATETIME              DEFAULT NULL COMMENT '锁定到期时间',
    `fail_count`  INT          NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT         COMMENT '商品描述',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock`       INT          NOT NULL DEFAULT 0 COMMENT '库存数量',
    `status`      INT          NOT NULL DEFAULT 0 COMMENT '状态: 0-上架, 1-下架',
    `image_url`   VARCHAR(255) DEFAULT NULL COMMENT '商品图片URL',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '购物车条目ID',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT       NOT NULL COMMENT '商品ID',
    `quantity`    INT          NOT NULL DEFAULT 1 COMMENT '数量',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单编号',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
    `total_amount`  DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    `status`        INT          NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待确认, 1-已确认, 2-已付款, 3-已发货, 4-已完成, 5-已取消',
    `recipient_name`  VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    `recipient_phone` VARCHAR(20)  NOT NULL COMMENT '收货人电话',
    `recipient_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
    `paid_at`       DATETIME     DEFAULT NULL COMMENT '付款时间',
    `shipped_at`    DATETIME     DEFAULT NULL COMMENT '发货时间',
    `completed_at`  DATETIME     DEFAULT NULL COMMENT '完成时间',
    `cancelled_at`  DATETIME     DEFAULT NULL COMMENT '取消时间',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`    BIGINT       NOT NULL COMMENT '订单ID',
    `product_id`  BIGINT       NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称(快照)',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品单价(快照)',
    `quantity`    INT          NOT NULL COMMENT '购买数量',
    `subtotal`    DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- 插入默认管理员账号 (admin / Admin@123456)
INSERT INTO `user` (`username`, `password`, `role`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 0);
