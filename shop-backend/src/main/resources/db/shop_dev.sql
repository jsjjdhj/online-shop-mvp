-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: shop_dev
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `shop_dev`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `shop_dev` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `shop_dev`;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '?????ID',
  `user_id` bigint NOT NULL COMMENT '??ID',
  `product_id` bigint NOT NULL COMMENT '??ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '??',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `order_id` bigint NOT NULL COMMENT '??ID',
  `product_id` bigint NOT NULL COMMENT '??ID',
  `product_name` varchar(100) NOT NULL COMMENT '????(??)',
  `product_price` decimal(10,2) NOT NULL COMMENT '????(??)',
  `quantity` int NOT NULL COMMENT '????',
  `subtotal` decimal(12,2) NOT NULL COMMENT '????',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `order_no` varchar(32) NOT NULL COMMENT '????',
  `user_id` bigint NOT NULL COMMENT '??ID',
  `total_amount` decimal(12,2) NOT NULL COMMENT '?????',
  `status` int NOT NULL DEFAULT '0' COMMENT '????: 0-???, 1-???, 2-???, 3-???, 4-???, 5-???',
  `recipient_name` varchar(50) NOT NULL COMMENT '?????',
  `recipient_phone` varchar(20) NOT NULL COMMENT '?????',
  `recipient_address` varchar(255) NOT NULL COMMENT '????',
  `paid_at` datetime DEFAULT NULL COMMENT '????',
  `shipped_at` datetime DEFAULT NULL COMMENT '????',
  `completed_at` datetime DEFAULT NULL COMMENT '????',
  `cancelled_at` datetime DEFAULT NULL COMMENT '????',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `name` varchar(100) NOT NULL COMMENT '????',
  `description` text COMMENT '????',
  `price` decimal(10,2) NOT NULL COMMENT '????',
  `stock` int NOT NULL DEFAULT '0' COMMENT '????',
  `status` int NOT NULL DEFAULT '0' COMMENT '??: 0-??, 1-??',
  `image_url` varchar(255) DEFAULT NULL COMMENT '????URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'?? MateBook 14 ?????','14??2K???????????Ultra 5????16GB???1TB????',6999.00,50,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(2,'Apple iPhone 15 Pro','??????A17 Pro???4800????????USB-C',8999.00,30,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(3,'?? Redmi K70 ??','?????8?2K????120W???5000mAh???',2499.00,100,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(4,'?? WH-1000XM5 ???????','????????30?????Hi-Res???????',2999.00,25,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(5,'?? ThinkPad X1 Carbon Gen 11','14??2.8K OLED??i7-1360P?16GB?512GB???1.12kg',10999.00,15,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(6,'?? U2723QE 4K???','27??IPS Black???4K????90W Type-C???HDR400',4299.00,20,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(7,'?? MX Master 3S ????','MagSpeed?????8K DPI??????USB-C???70???',899.00,80,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(8,'?? 990 PRO ???? 2TB','PCIe 4.0 NVMe M.2?????7450MB/s???6900MB/s',1599.00,40,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(9,'??? ?????? HX9352','???????5???????????????',499.00,200,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(10,'??? ??????????','????+???+??????????????',299.00,150,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(11,'???? ????? 1.2kg','???????????????????10???',198.00,500,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0),(12,'??? ??????? 250g','???????????????????',128.00,300,0,NULL,'2026-06-16 13:35:28','2026-06-16 13:35:28',0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `username` varchar(50) NOT NULL COMMENT '???',
  `password` varchar(255) NOT NULL COMMENT '??(BCrypt??)',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '??: USER-????, ADMIN-???',
  `status` int NOT NULL DEFAULT '0' COMMENT '??: 0-??, 1-??',
  `lock_time` datetime DEFAULT NULL COMMENT '??????',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '????????',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '????: 0-???, 1-???',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2b$10$Glfso9//mMW669BKSIm3febT0LpSxJKhmwRRdgXWRNLC6rfWTOeku','ADMIN',0,'2026-06-16 13:52:52',0,'2026-06-16 13:26:02','2026-06-16 13:40:14',0),(2,'testuser','$2b$10$Glfso9//mMW669BKSIm3febT0LpSxJKhmwRRdgXWRNLC6rfWTOeku','USER',0,NULL,0,'2026-06-16 13:35:28','2026-06-16 13:40:14',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'shop_dev'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-16 20:39:49
