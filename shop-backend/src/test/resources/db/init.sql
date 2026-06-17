-- ============================================================
-- 在线购物平台 MVP - 数据库初始化脚本（H2 测试版）
-- 软件质量与测试课 2025-2026-2 学期
-- ============================================================

-- 注意：使用 DROP 确保每次上下文加载时重新创建（H2 内存模式）

DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(255) NOT NULL,
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    `status`      INT          NOT NULL DEFAULT 0,
    `lock_time`   DATETIME              DEFAULT NULL,
    `fail_count`  INT          NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted`     INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
);

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price`       DECIMAL(10,2) NOT NULL,
    `stock`       INT          NOT NULL DEFAULT 0,
    `status`      INT          NOT NULL DEFAULT 0,
    `image_url`   VARCHAR(255) DEFAULT NULL,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted`     INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `product_id`  BIGINT       NOT NULL,
    `quantity`    INT          NOT NULL DEFAULT 1,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted`     INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_cart_user_id` (`user_id`),
    KEY `idx_cart_product_id` (`product_id`),
    UNIQUE KEY `uk_cart_user_product` (`user_id`, `product_id`)
);

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `order_no`      VARCHAR(32)  NOT NULL,
    `user_id`       BIGINT       NOT NULL,
    `total_amount`  DECIMAL(12,2) NOT NULL,
    `status`        INT          NOT NULL DEFAULT 0,
    `recipient_name`  VARCHAR(50)  NOT NULL,
    `recipient_phone` VARCHAR(20)  NOT NULL,
    `recipient_address` VARCHAR(255) NOT NULL,
    `paid_at`       DATETIME     DEFAULT NULL,
    `shipped_at`    DATETIME     DEFAULT NULL,
    `completed_at`  DATETIME     DEFAULT NULL,
    `cancelled_at`  DATETIME     DEFAULT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted`       INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_orders_order_no` (`order_no`),
    KEY `idx_orders_user_id` (`user_id`)
);

-- 订单商品明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT       NOT NULL,
    `product_id`    BIGINT       NOT NULL,
    `product_name`  VARCHAR(100) NOT NULL,
    `product_price` DECIMAL(10,2) NOT NULL,
    `quantity`      INT          NOT NULL,
    `subtotal`      DECIMAL(12,2) NOT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_item_order_id` (`order_id`)
);

-- 插入默认管理员账号 (admin / Admin@123456)
INSERT INTO `user` (`username`, `password`, `role`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 0);
