-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: data-mall
-- ------------------------------------------------------
-- Server version	8.0.34

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
-- Table structure for table `auth`
--

DROP TABLE IF EXISTS `auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限路径',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `parent_id` int NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth`
--

LOCK TABLES `auth` WRITE;
INSERT INTO `auth` (`id`, `path`, `name`, `parent_id`, `create_time`, `update_time`) VALUES (1,'/','admin1',0,'2023-08-28 14:13:59',NULL),(2,'/user','普通用户目录',0,'2023-08-28 14:14:01',NULL),(6,'/','kef',1,'2023-08-28 14:14:02',NULL);
UNLOCK TABLES;

--
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `categories_id` int NOT NULL COMMENT '商品分类',
  `detail` text NOT NULL COMMENT '商品详情',
  `pic_index` varchar(200) NOT NULL COMMENT '商品主图',
  `price` int NOT NULL COMMENT '价格，单位分',
  `uid` int NOT NULL COMMENT '上传用户',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `state` int NOT NULL DEFAULT '0' COMMENT '商品状态0正常1下架-1冻结-2假删',
  PRIMARY KEY (`id`),
  KEY `categories_id` (`categories_id`),
  KEY `uid` (`uid`),
  CONSTRAINT `goods_ibfk_1` FOREIGN KEY (`categories_id`) REFERENCES `goods_categories` (`id`),
  CONSTRAINT `goods_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_base` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

LOCK TABLES `goods` WRITE;
INSERT INTO `goods` (`id`, `name`, `categories_id`, `detail`, `pic_index`, `price`, `uid`, `create_time`, `update_time`, `state`) VALUES (1,'test',1,'testtesttesttesttesttesttesttesttesttest','https://th.bing.com/th?id=OIP.bVb769JBdzVZYuksxZ2Y-AHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2',1,1,'2023-09-13 12:35:33','2023-09-13 12:35:36',0);
UNLOCK TABLES;

--
-- Table structure for table `goods_categories`
--

DROP TABLE IF EXISTS `goods_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '商品分类名称',
  `url` varchar(100) NOT NULL COMMENT '分类url',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `state` int NOT NULL DEFAULT '0' COMMENT '商品分类名称0正常1隐藏-2假删',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_categories`
--

LOCK TABLES `goods_categories` WRITE;
INSERT INTO `goods_categories` (`id`, `name`, `url`, `create_time`, `update_time`, `state`) VALUES (1,'经济','economic','2023-08-31 06:23:30',NULL,0),(2,'政治','politics','2023-08-31 06:30:25',NULL,0),(3,'环境','environment','2023-08-31 06:30:47',NULL,0),(4,'法律','law','2023-09-05 13:33:15',NULL,0),(5,'arcgis','arcgis','2023-09-05 13:33:50',NULL,0),(6,'生物','bio','2023-09-05 13:34:11',NULL,0);
UNLOCK TABLES;

--
-- Table structure for table `goods_collection`
--

DROP TABLE IF EXISTS `goods_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_collection` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL COMMENT '用户id',
  `goods_id` int NOT NULL COMMENT '商品id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `goods_id` (`goods_id`),
  CONSTRAINT `goods_collection_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_base` (`id`),
  CONSTRAINT `goods_collection_ibfk_2` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_collection`
--

LOCK TABLES `goods_collection` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `goods_comment`
--

DROP TABLE IF EXISTS `goods_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL COMMENT '用户id',
  `goods_id` int NOT NULL COMMENT '商品id',
  `message` text NOT NULL COMMENT '商品评论',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `state` int NOT NULL DEFAULT '0' COMMENT '0正常-1冻结',
  `parent_id` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `goods_id` (`goods_id`),
  CONSTRAINT `goods_comment_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_base` (`id`),
  CONSTRAINT `goods_comment_ibfk_2` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_comment`
--

LOCK TABLES `goods_comment` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `goods_pic`
--

DROP TABLE IF EXISTS `goods_pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_pic` (
  `id` int NOT NULL AUTO_INCREMENT,
  `goods_id` int NOT NULL COMMENT '商品id',
  `url` varchar(200) NOT NULL COMMENT '商品图片url',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `states` int NOT NULL DEFAULT '0' COMMENT '图片状态0正常-1冻结-2假删',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`),
  CONSTRAINT `goods_pic_ibfk_1` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_pic`
--

LOCK TABLES `goods_pic` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_pk2` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT INTO `role` (`id`, `role_name`, `create_time`, `update_time`) VALUES (1,'admin','2023-08-28 14:17:25',NULL),(2,'user1','2023-08-28 14:17:26',NULL);
UNLOCK TABLES;

--
-- Table structure for table `role_to_auth`
--

DROP TABLE IF EXISTS `role_to_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_to_auth` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限角色对应id',
  `role_id` int NOT NULL COMMENT '角色id',
  `auth_id` int DEFAULT NULL COMMENT '权限id',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `roleToAuth_auth_id_fk` (`auth_id`),
  KEY `roleToAuth_role_id_fk` (`role_id`),
  CONSTRAINT `roleToAuth_auth_id_fk` FOREIGN KEY (`auth_id`) REFERENCES `auth` (`id`),
  CONSTRAINT `roleToAuth_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限角色对应表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_to_auth`
--

LOCK TABLES `role_to_auth` WRITE;
INSERT INTO `role_to_auth` (`id`, `role_id`, `auth_id`, `create_time`, `update_time`) VALUES (1,1,1,'2023-09-14 02:43:54',NULL),(2,2,2,'2023-09-14 02:43:58',NULL);
UNLOCK TABLES;

--
-- Table structure for table `user_base`
--

DROP TABLE IF EXISTS `user_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_base` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `pass_word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `token` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'token',
  `state` int DEFAULT '0' COMMENT '用户状态（0正常1冻结2假删）',
  `role` int DEFAULT '2' COMMENT '角色id',
  `create_time` timestamp NOT NULL COMMENT '注册时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `user_base_role_id_fk` (`role`),
  CONSTRAINT `user_base_role_id_fk` FOREIGN KEY (`role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='基础用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_base`
--

LOCK TABLES `user_base` WRITE;
INSERT INTO `user_base` (`id`, `user_name`, `pass_word`, `email`, `token`, `state`, `role`, `create_time`, `update_time`) VALUES (1,'admin','123','admin@admin.qq','2c6196f2b01a795bda0d6111e2e1065c1b309f22abed9f48e36717f9d3987c25',0,1,'2023-08-31 07:38:08','2023-08-31 07:49:22'),(3,'wooovi','Woov0815','wooovi@qq.com',NULL,0,1,'2023-06-04 01:56:29','2023-06-04 07:53:12'),(5,'罗海若','123456','276070095@qq.com','e4b88e8d6fd9783134e219dff6f4969da0fdf3f91493993c7150a7402a6fa38d',0,1,'2023-06-10 07:25:14','2023-06-11 07:53:57'),(22,'张伟',NULL,'330518952@qq.com',NULL,0,1,'2023-06-18 02:25:02',NULL),(24,'woov','123456','2867119837@qq.com',NULL,0,2,'2023-06-18 07:14:51',NULL);
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-14 11:14:03
