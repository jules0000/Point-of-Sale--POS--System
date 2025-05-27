-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 27, 2025 at 07:21 AM
-- Server version: 8.3.0
-- PHP Version: 7.3.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pos_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `action_type` varchar(50) NOT NULL,
  `action_details` text,
  `ip_address` varchar(45) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `audit_log`
--

INSERT INTO `audit_log` (`id`, `user_id`, `action_type`, `action_details`, `ip_address`, `created_at`) VALUES
('A001', 'U001', 'LOGIN', 'Admin logged in', '127.0.0.1', '2025-05-21 04:45:57');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `id` varchar(36) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`, `created_at`, `updated_at`) VALUES
('C001', 'Food', 'Food items and snacks', '2025-05-21 04:09:59', '2025-05-21 04:09:59'),
('C002', 'Beverages', 'Drinks and beverages', '2025-05-21 04:09:59', '2025-05-21 04:09:59'),
('C003', 'Household', 'Household items', '2025-05-21 04:09:59', '2025-05-21 04:09:59'),
('C004', 'Cat Food', 'Cat Food', '2025-05-21 15:00:48', '2025-05-21 15:00:48');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
CREATE TABLE IF NOT EXISTS `customers` (
  `id` varchar(36) NOT NULL,
  `membership_id` varchar(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text,
  `points` int DEFAULT '0',
  `total_spent` decimal(10,2) DEFAULT '0.00',
  `level` varchar(20) DEFAULT 'Regular',
  `membership_level` varchar(20) DEFAULT 'Basic',
  `join_date` date DEFAULT NULL,
  `valid_until` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `membership_id` (`membership_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `membership_id`, `name`, `contact_number`, `email`, `address`, `points`, `total_spent`, `level`, `membership_level`, `join_date`, `valid_until`, `is_active`, `created_at`, `updated_at`) VALUES
('4623aac7-10a5-427a-9fe2-38b029b49118', '0122769725', 'Darwin Darca', '09090909', 'darwin@gmail.com', 'ustlegazpi', 112, 1277.80, 'Regular', 'Silver', '2025-05-21', '2026-05-21', 1, '2025-05-21 08:22:45', '2025-05-22 02:08:39'),
('WALKIN', NULL, 'Walk-in Customer', NULL, NULL, NULL, 0, 0.00, 'Regular', 'Basic', NULL, NULL, 0, '2025-05-21 13:49:35', '2025-05-22 00:28:35'),
('fef7077c-b488-4f90-a407-9c7718cc1dba', '45435353', 'jols', '09090909090', 'jols@gmail.com', '090909 where', 232, 2329.20, 'Regular', 'Gold', '2025-05-22', '2026-05-22', 1, '2025-05-22 00:29:03', '2025-05-22 00:38:03'),
('9c7b7fb8-7d6d-4076-b3cd-5994553473fe', 'DD0146528', 'chiska', '090909', 'chiska@gmail.com', 'sm', 6270, 62714.28, 'Regular', 'Gold', '2025-05-23', '2026-05-23', 1, '2025-05-23 04:34:51', '2025-05-23 04:41:50');

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` varchar(36) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `description` text,
  `barcode` varchar(50) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT '0.00',
  `price` decimal(10,2) DEFAULT '0.00',
  `quantity` int DEFAULT '0',
  `reorder_level` int DEFAULT '5',
  `is_active` tinyint(1) DEFAULT '1',
  `image_path` varchar(255) DEFAULT NULL,
  `image` longblob,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`id`, `item_name`, `description`, `barcode`, `category`, `cost_price`, `price`, `quantity`, `reorder_level`, `is_active`, `image_path`, `image`) VALUES
('I001', 'Bread', 'Fresh bread', '12345678907', 'Food', 30.00, 45.00, 52, 10, 1, NULL, NULL),
('I002', 'Milk', 'Dairy milk', '0122769725', 'Food', 50.00, 65.00, 42, 10, 1, NULL, NULL),
('83e9b8ef-4d54-48ee-9c1a-1a21b44552a9', 'shabu', '', '8850124045461', 'Beverages', 9999.00, 9999.00, 74, 0, 1, '', NULL),
('ed383ce2-3309-4703-8232-2a35a6686578', 'kape', 'migrain', '312312313', 'Food', 10.00, 10.00, 25, 10, 1, NULL, NULL),
('feb27ca3-3d13-46e1-bfec-cc6ccc481245', 'Whiskas', '1.5kg', '545345433', 'Cat Food', 250.00, 250.00, 85, 10, 1, '\"C:\\Users\\Acer\\Downloads\\starbucks_icon.png\"', NULL),
('b8b96f5e-4d15-4686-9fe6-8c191e60c490', 'Birch Tree', '33g', '748485401492', 'Food', 14.00, 14.00, 45, 10, 1, '', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `inventory_history`
--

DROP TABLE IF EXISTS `inventory_history`;
CREATE TABLE IF NOT EXISTS `inventory_history` (
  `id` varchar(36) NOT NULL,
  `item_id` varchar(36) NOT NULL,
  `previous_quantity` int NOT NULL,
  `new_quantity` int NOT NULL,
  `change_type` enum('Purchase','Sale','Adjustment','Return') NOT NULL,
  `reference_id` varchar(36) DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inventory_history_item` (`item_id`),
  KEY `idx_inventory_history_date` (`created_at`),
  KEY `idx_inventory_history_type` (`change_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `member_points_history`
--

DROP TABLE IF EXISTS `member_points_history`;
CREATE TABLE IF NOT EXISTS `member_points_history` (
  `id` varchar(36) NOT NULL,
  `member_id` varchar(36) NOT NULL,
  `points_change` int NOT NULL,
  `transaction_id` varchar(36) DEFAULT NULL,
  `reason` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_member_points_member` (`member_id`),
  KEY `idx_member_points_date` (`created_at`),
  KEY `idx_member_points_reason` (`reason`),
  KEY `idx_member_points_member_date` (`member_id`,`created_at`),
  KEY `idx_member_points_transaction` (`transaction_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `points_history`
--

DROP TABLE IF EXISTS `points_history`;
CREATE TABLE IF NOT EXISTS `points_history` (
  `id` varchar(36) NOT NULL,
  `membership_id` varchar(20) DEFAULT NULL,
  `transaction_id` varchar(36) NOT NULL,
  `points_before` decimal(10,2) NOT NULL,
  `points_change` decimal(10,2) NOT NULL,
  `points_after` decimal(10,2) NOT NULL,
  `type` enum('Earned','Redeemed','Expired','Adjusted') NOT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_points_history_member` (`membership_id`),
  KEY `idx_points_history_transaction` (`transaction_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `points_history`
--

INSERT INTO `points_history` (`id`, `membership_id`, `transaction_id`, `points_before`, `points_change`, `points_after`, `type`, `notes`, `created_at`) VALUES
('0dab1d66-2a52-4fcb-92e8-b109af647767', '0122769725', 'TD948845A8C', 49.00, 10.00, 59.00, 'Earned', NULL, '2025-05-21 23:38:38'),
('c6a134bf-7f0d-4414-81e2-407a21082942', '0122769725', 'T7BF547C596', 59.00, 15.00, 74.00, 'Earned', NULL, '2025-05-21 23:44:17'),
('43c8f0c4-3084-4c11-a3d3-b8c473157670', '0122769725', 'T8AEFCB868F', 74.00, 10.00, 84.00, 'Earned', NULL, '2025-05-21 23:47:21'),
('75af82e0-a80a-4bd9-9364-e3dde89e38ce', '0122769725', 'TDD70269BAE', 84.00, 5.00, 89.00, 'Earned', NULL, '2025-05-21 23:59:10'),
('7d401b88-d009-42d9-9a60-d591efcc126b', '0122769725', 'TACD3C57475', 89.00, 10.00, 99.00, 'Earned', NULL, '2025-05-22 00:01:58'),
('9801b32c-ff9a-4e36-9590-c85b5393012b', '0122769725', 'TAC98CD04BC', 99.00, 2.00, 101.00, 'Earned', NULL, '2025-05-22 00:11:17'),
('6d44bd72-64e4-4ff5-8cc4-408218cf2d3a', '0122769725', 'T5A679F0FDC', 101.00, 10.00, 111.00, 'Earned', NULL, '2025-05-22 00:23:13'),
('e6f98887-bed7-422a-805c-c0ea19193235', '45435353', 'T33A68CF72D', 0.00, 224.00, 224.00, 'Earned', NULL, '2025-05-22 00:29:45'),
('006aeaa5-aa73-49e6-a47e-3b0ce5692653', '45435353', 'T93DC5C39DD', 224.00, 1.00, 225.00, 'Earned', NULL, '2025-05-22 00:30:20'),
('0d3c6b55-17b4-4bc5-bec2-74aba097b765', '45435353', 'T93DC5C39DD', 225.00, 0.00, 225.00, 'Redeemed', NULL, '2025-05-22 00:30:20'),
('ff64e531-75ac-4fd7-8c8d-36605797e230', '45435353', 'T0EF110FDD9', 225.00, 7.00, 232.00, 'Earned', NULL, '2025-05-22 00:38:03'),
('f66533f5-4a7b-4dfb-a83b-72c98daf9eae', '0122769725', 'T6EC9EB5F3D', 111.00, 1.00, 112.00, 'Earned', NULL, '2025-05-22 02:08:39'),
('0e267134-6420-461c-9c65-38aedbc75987', '0122769725', 'T6EC9EB5F3D', 112.00, 0.00, 112.00, 'Redeemed', NULL, '2025-05-22 02:08:39'),
('4c4f4b29-ad82-4494-9ee7-18110c844e7f', 'DD0146528', 'T3CD2B50869', 0.00, 4479.00, 4479.00, 'Earned', NULL, '2025-05-23 04:39:52'),
('e05dc011-b34d-4aa7-8878-8c6432567590', 'DD0146528', 'TDAFDC97C18', 4479.00, 1791.00, 6270.00, 'Earned', NULL, '2025-05-23 04:41:50'),
('369bca60-56d4-4935-861a-51ae93c136b2', 'DD0146528', 'TDAFDC97C18', 6270.00, 0.00, 6270.00, 'Redeemed', NULL, '2025-05-23 04:41:50');

-- --------------------------------------------------------

--
-- Table structure for table `printer_config`
--

DROP TABLE IF EXISTS `printer_config`;
CREATE TABLE IF NOT EXISTS `printer_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `printer_name` varchar(255) NOT NULL,
  `paper_size` varchar(50) NOT NULL,
  `auto_print` tinyint(1) DEFAULT '0',
  `copies` int DEFAULT '1',
  `paper_type` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `printer_config`
--

INSERT INTO `printer_config` (`id`, `printer_name`, `paper_size`, `auto_print`, `copies`, `paper_type`, `created_at`, `updated_at`) VALUES
(1, 'POS-58(copy of 2)', 'Thermal 58mm', 1, 1, 'Thermal', '2025-05-21 05:08:36', '2025-05-21 20:09:20');

-- --------------------------------------------------------

--
-- Table structure for table `receipt_design`
--

DROP TABLE IF EXISTS `receipt_design`;
CREATE TABLE IF NOT EXISTS `receipt_design` (
  `id` int NOT NULL AUTO_INCREMENT,
  `header_text` text,
  `footer_text` text,
  `show_logo` tinyint(1) DEFAULT '0',
  `logo_path` varchar(255) DEFAULT NULL,
  `show_date_time` tinyint(1) DEFAULT '1',
  `show_cashier_name` tinyint(1) DEFAULT '1',
  `show_tax_details` tinyint(1) DEFAULT '1',
  `font_family` varchar(100) DEFAULT 'Arial',
  `font_size` int DEFAULT '12',
  `show_border` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `location` text,
  `contact_number` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `receipt_design`
--

INSERT INTO `receipt_design` (`id`, `header_text`, `footer_text`, `show_logo`, `logo_path`, `show_date_time`, `show_cashier_name`, `show_tax_details`, `font_family`, `font_size`, `show_border`, `created_at`, `updated_at`, `location`, `contact_number`) VALUES
(1, 'Welcome to Our Store', 'Thank you for shopping!', 1, '', 1, 1, 1, 'Arial', 12, 0, '2025-05-21 05:08:36', '2025-05-21 19:22:36', NULL, NULL),
(2, 'QuickVend POS', 'Thank you for you purchase!', 0, '', 1, 1, 1, 'Times New Roman', 12, 0, '2025-05-21 20:09:20', '2025-05-21 20:09:20', 'Legazpi City, Daraga', '09458520450');

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
CREATE TABLE IF NOT EXISTS `reports` (
  `id` varchar(36) NOT NULL,
  `report_name` varchar(100) NOT NULL,
  `report_type` varchar(50) NOT NULL,
  `description` text,
  `query_template` text NOT NULL,
  `parameters` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `category` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_report_name` (`report_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `returns`
--

DROP TABLE IF EXISTS `returns`;
CREATE TABLE IF NOT EXISTS `returns` (
  `id` varchar(36) NOT NULL,
  `transaction_id` varchar(36) NOT NULL,
  `return_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cashier_id` varchar(36) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `reason` text,
  `status` varchar(20) DEFAULT 'Completed',
  PRIMARY KEY (`id`),
  KEY `transaction_id` (`transaction_id`),
  KEY `cashier_id` (`cashier_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `returns`
--

INSERT INTO `returns` (`id`, `transaction_id`, `return_date`, `cashier_id`, `total_amount`, `reason`, `status`) VALUES
('R001', 'T001', '2025-05-21 04:10:23', 'U002', 45.00, 'Damaged item', 'Completed');

-- --------------------------------------------------------

--
-- Table structure for table `return_items`
--

DROP TABLE IF EXISTS `return_items`;
CREATE TABLE IF NOT EXISTS `return_items` (
  `id` varchar(36) NOT NULL,
  `return_id` varchar(36) NOT NULL,
  `product_id` varchar(36) NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `return_id` (`return_id`),
  KEY `product_id` (`product_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `return_items`
--

INSERT INTO `return_items` (`id`, `return_id`, `product_id`, `quantity`, `unit_price`, `subtotal`) VALUES
('RI001', 'R001', 'I001', 1, 45.00, 45.00);

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
CREATE TABLE IF NOT EXISTS `settings` (
  `id` varchar(36) NOT NULL,
  `setting_key` varchar(50) NOT NULL,
  `setting_value` text NOT NULL,
  `setting_type` varchar(20) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `setting_key` (`setting_key`),
  KEY `idx_settings_key` (`setting_key`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` varchar(36) NOT NULL,
  `transaction_no` varchar(20) NOT NULL,
  `cashier_id` varchar(36) NOT NULL,
  `member_id` varchar(36) DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `discount_amount` decimal(10,2) DEFAULT '0.00',
  `tax_amount` decimal(10,2) DEFAULT '0.00',
  `total_amount` decimal(10,2) NOT NULL,
  `points_earned` decimal(10,2) DEFAULT '0.00',
  `points_redeemed` decimal(10,2) DEFAULT '0.00',
  `payment_method` enum('Cash','Card','GCash','Maya') NOT NULL,
  `amount_tendered` decimal(10,2) NOT NULL,
  `change_amount` decimal(10,2) NOT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Completed','Voided','Refunded') DEFAULT 'Completed',
  `void_reason` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transaction_no` (`transaction_no`),
  KEY `idx_transactions_date` (`transaction_date`),
  KEY `idx_transactions_cashier` (`cashier_id`),
  KEY `idx_transactions_member` (`member_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `transaction_no`, `cashier_id`, `member_id`, `subtotal`, `discount_amount`, `tax_amount`, `total_amount`, `points_earned`, `points_redeemed`, `payment_method`, `amount_tendered`, `change_amount`, `transaction_date`, `status`, `void_reason`, `created_at`, `updated_at`) VALUES
('T001', 'TXN001', 'U002', 'M001', 100.00, 0.00, 12.00, 112.00, 10.00, 0.00, 'Cash', 120.00, 8.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T002', 'TXN002', 'U002', 'M002', 200.00, 10.00, 24.00, 214.00, 20.00, 5.00, 'Card', 220.00, 6.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TD17356D3A6', 'R06168286', 'cashier1', NULL, 130.00, 0.00, 15.60, 145.60, 0.00, 0.00, 'Cash', 200.00, 54.40, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TA004BFBF4F', 'R07849019', 'cashier1', NULL, 150.00, 0.00, 18.00, 168.00, 0.00, 0.00, 'Cash', 200.00, 32.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T3AC42864BA', 'R07264867', 'cashier1', NULL, 130.00, 0.00, 15.60, 145.60, 0.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TEC0C6C93E2', 'R06579889', 'cashier1', NULL, 130.00, 0.00, 15.60, 145.60, 0.00, 0.00, 'Cash', 500.00, 354.40, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T9C4C6AC898', 'R05168926', 'cashier1', NULL, 65.00, 0.00, 7.80, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TA7D6079150', 'R07052688', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TEB535A8E07', 'R04284291', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T03560C841E', 'R01730822', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TDC82123430', 'R04179286', 'cashier', NULL, 500.00, 0.00, 0.00, 560.00, 0.00, 0.00, 'Cash', 800.00, 240.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T64E8C9ED77', 'R00296265', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T0401E1D8ED', 'R08619668', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TB39B891A8A', 'R08199802', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 100.00, 27.20, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T3907EA0B21', 'R04797581', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('T3907EA0B22', 'R04797582', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 15:40:26', 'Completed', NULL, '2025-05-21 15:40:26', '2025-05-21 15:40:26'),
('TB1FF87CA9C', 'R04301143', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 18:30:20', 'Completed', NULL, '2025-05-21 18:30:20', '2025-05-21 18:30:20'),
('T0ED51F5ABA', 'R03178700', 'cashier', NULL, 125.00, 0.00, 0.00, 140.00, 0.00, 0.00, 'Cash', 150.00, 10.00, '2025-05-21 19:04:03', 'Completed', NULL, '2025-05-21 19:04:03', '2025-05-21 19:04:03'),
('T090E9AF76C', 'R03105128', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:14:44', 'Completed', NULL, '2025-05-21 19:14:44', '2025-05-21 19:14:44'),
('T2335730385', 'R02391992', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:16:53', 'Completed', NULL, '2025-05-21 19:16:53', '2025-05-21 19:16:53'),
('TCB120C682C', 'R01437380', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:19:43', 'Completed', NULL, '2025-05-21 19:19:43', '2025-05-21 19:19:43'),
('T4453AF6BFE', 'R02565513', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:21:27', 'Completed', NULL, '2025-05-21 19:21:27', '2025-05-21 19:21:27'),
('T7635FADD42', 'R05614888', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:22:54', 'Completed', NULL, '2025-05-21 19:22:54', '2025-05-21 19:22:54'),
('T69EBB841E3', 'R03525988', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:30:54', 'Completed', NULL, '2025-05-21 19:30:54', '2025-05-21 19:30:54'),
('TAD756C061C', 'R03187116', 'cashier', NULL, 130.00, 0.00, 0.00, 145.60, 0.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 19:32:31', 'Completed', NULL, '2025-05-21 19:32:31', '2025-05-21 19:32:31'),
('T759DF43D81', 'R03215013', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-21 19:33:53', 'Completed', NULL, '2025-05-21 19:33:53', '2025-05-21 19:33:53'),
('T128F5B45D2', 'R01061710', 'cashier', NULL, 10.00, 0.00, 0.00, 11.20, 0.00, 0.00, 'Cash', 20.00, 8.80, '2025-05-21 19:35:22', 'Completed', NULL, '2025-05-21 19:35:22', '2025-05-21 19:35:22'),
('T594D6933B6', 'R02080891', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 55.00, 4.60, '2025-05-21 19:36:09', 'Completed', NULL, '2025-05-21 19:36:09', '2025-05-21 19:36:09'),
('TB6349AE99B', 'R05810605', 'cashier', NULL, 565.00, 0.00, 0.00, 632.80, 0.00, 0.00, 'Cash', 700.00, 67.20, '2025-05-21 19:37:52', 'Completed', NULL, '2025-05-21 19:37:52', '2025-05-21 19:37:52'),
('T43BC922D5E', 'R00216482', 'cashier', NULL, 185.00, 0.00, 0.00, 207.20, 0.00, 0.00, 'Cash', 250.00, 42.80, '2025-05-21 19:39:13', 'Completed', NULL, '2025-05-21 19:39:13', '2025-05-21 19:39:13'),
('T1C0B507827', 'R05983415', 'cashier', NULL, 120.00, 0.00, 0.00, 134.40, 0.00, 0.00, 'Cash', 150.00, 15.60, '2025-05-21 19:42:03', 'Completed', NULL, '2025-05-21 19:42:03', '2025-05-21 19:42:03'),
('T1B024EB410', 'R02776140', 'cashier', NULL, 340.00, 0.00, 0.00, 380.80, 0.00, 0.00, 'Cash', 400.00, 19.20, '2025-05-21 19:44:44', 'Completed', NULL, '2025-05-21 19:44:44', '2025-05-21 19:44:44'),
('T8817929874', 'R06564800', 'cashier', NULL, 10.00, 0.00, 0.00, 11.20, 0.00, 0.00, 'Cash', 20.00, 8.80, '2025-05-21 19:45:54', 'Completed', NULL, '2025-05-21 19:45:54', '2025-05-21 19:45:54'),
('T80B1ABB173', 'R07488183', 'cashier', NULL, 130.00, 0.00, 0.00, 145.60, 0.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 19:48:24', 'Completed', NULL, '2025-05-21 19:48:24', '2025-05-21 19:48:24'),
('T664B229AA1', 'R06895889', 'cashier', NULL, 130.00, 0.00, 0.00, 145.60, 0.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 19:54:17', 'Completed', NULL, '2025-05-21 19:54:17', '2025-05-21 19:54:17'),
('TE99E8717A3', 'R05375714', 'cashier', NULL, 20.00, 0.00, 0.00, 22.40, 0.00, 0.00, 'Cash', 25.00, 2.60, '2025-05-21 19:58:05', 'Completed', NULL, '2025-05-21 19:58:05', '2025-05-21 19:58:05'),
('TC4CD637080', 'R06932518', 'cashier', NULL, 40.00, 0.00, 0.00, 44.80, 0.00, 0.00, 'Cash', 50.00, 5.20, '2025-05-21 20:04:14', 'Completed', NULL, '2025-05-21 20:04:14', '2025-05-21 20:04:14'),
('TA59090DEB9', 'R01009095', 'cashier', NULL, 20.00, 0.00, 0.00, 22.40, 0.00, 0.00, 'Cash', 25.00, 2.60, '2025-05-21 20:09:43', 'Completed', NULL, '2025-05-21 20:09:43', '2025-05-21 20:09:43'),
('TA96B23BD78', 'R09873564', 'cashier', NULL, 500.00, 0.00, 0.00, 560.00, 0.00, 0.00, 'Cash', 560.00, 0.00, '2025-05-21 20:14:19', 'Completed', NULL, '2025-05-21 20:14:19', '2025-05-21 20:14:19'),
('T2EDC6EC5D9', 'R05578406', 'cashier', NULL, 260.00, 0.00, 0.00, 291.20, 0.00, 0.00, 'Cash', 300.00, 8.80, '2025-05-21 20:16:48', 'Completed', NULL, '2025-05-21 20:16:48', '2025-05-21 20:16:48'),
('TB997AE120B', 'R05030806', 'cashier', NULL, 250.00, 0.00, 0.00, 280.00, 0.00, 0.00, 'Cash', 300.00, 20.00, '2025-05-21 20:22:28', 'Completed', NULL, '2025-05-21 20:22:28', '2025-05-21 20:22:28'),
('TDD725D1E2D', 'R01156655', 'cashier', NULL, 110.00, 0.00, 0.00, 123.20, 0.00, 0.00, 'Cash', 130.00, 6.80, '2025-05-21 20:23:22', 'Completed', NULL, '2025-05-21 20:23:22', '2025-05-21 20:23:22'),
('T496A667ECA', 'R02502140', 'cashier', NULL, 110.00, 0.00, 0.00, 123.20, 0.00, 0.00, 'Cash', 130.00, 6.80, '2025-05-21 20:24:28', 'Completed', NULL, '2025-05-21 20:24:28', '2025-05-21 20:24:28'),
('T894D6E54C2', 'R04466530', 'cashier', NULL, 120.00, 0.00, 0.00, 134.40, 0.00, 0.00, 'Cash', 135.00, 0.60, '2025-05-21 20:27:37', 'Completed', NULL, '2025-05-21 20:27:37', '2025-05-21 20:27:37'),
('TE655EBB2D8', 'R02682983', 'cashier', NULL, 185.00, 0.00, 0.00, 207.20, 0.00, 0.00, 'Cash', 210.00, 2.80, '2025-05-21 20:36:12', 'Completed', NULL, '2025-05-21 20:36:12', '2025-05-21 20:36:12'),
('T89C498E342', 'R05017383', 'cashier', NULL, 370.00, 0.00, 0.00, 414.40, 0.00, 0.00, 'Cash', 450.00, 35.60, '2025-05-21 20:39:56', 'Completed', NULL, '2025-05-21 20:39:56', '2025-05-21 20:39:56'),
('T5C922D1959', 'R09070703', 'cashier', NULL, 370.00, 0.00, 0.00, 414.40, 0.00, 0.00, 'Cash', 450.00, 35.60, '2025-05-21 20:42:43', 'Completed', NULL, '2025-05-21 20:42:43', '2025-05-21 20:42:43'),
('T3DA30FFFC0', 'R08686892', 'cashier', NULL, 20.00, 0.00, 0.00, 22.40, 0.00, 0.00, 'Cash', 30.00, 7.60, '2025-05-21 23:06:31', 'Completed', NULL, '2025-05-21 23:06:31', '2025-05-21 23:06:31'),
('TCAA1A32279', 'R03699701', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 30.00, 0.00, 0.00, 33.60, 3.00, 0.00, 'Cash', 40.00, 6.40, '2025-05-21 23:09:33', 'Completed', NULL, '2025-05-21 23:09:33', '2025-05-21 23:09:33'),
('T3C9B58809F', 'R01059247', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 30.00, 0.00, 0.00, 33.60, 3.00, 0.00, 'Cash', 40.00, 6.40, '2025-05-21 23:12:25', 'Completed', NULL, '2025-05-21 23:12:25', '2025-05-21 23:12:25'),
('T715FEA7AD5', 'R07386425', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 30.00, 0.00, 0.00, 33.60, 3.00, 0.00, 'Cash', 40.00, 6.40, '2025-05-21 23:12:29', 'Completed', NULL, '2025-05-21 23:12:29', '2025-05-21 23:12:29'),
('T01C4E2B7B8', 'R04723151', 'cashier', NULL, 20.00, 0.00, 0.00, 22.40, 0.00, 0.00, 'Cash', 30.00, 7.60, '2025-05-21 23:13:33', 'Completed', NULL, '2025-05-21 23:13:33', '2025-05-21 23:13:33'),
('T61CF7E35BE', 'R06003150', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 50.00, 0.00, 0.00, 56.00, 5.00, 0.00, 'Cash', 60.00, 4.00, '2025-05-21 23:13:52', 'Completed', NULL, '2025-05-21 23:13:52', '2025-05-21 23:13:52'),
('T0F9A30AC0B', 'R01657972', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 50.00, 0.00, 0.00, 56.00, 5.00, 0.00, 'Cash', 60.00, 4.00, '2025-05-21 23:16:17', 'Completed', NULL, '2025-05-21 23:16:17', '2025-05-21 23:16:17'),
('T1FA264D1E8', 'R05162033', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 90.00, 0.00, 0.00, 100.80, 10.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-21 23:16:57', 'Completed', NULL, '2025-05-21 23:16:57', '2025-05-21 23:16:57'),
('TE99B255803', 'R01529653', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 135.00, 0.00, 0.00, 151.20, 15.00, 0.00, 'Cash', 160.00, 8.80, '2025-05-21 23:25:11', 'Completed', NULL, '2025-05-21 23:25:11', '2025-05-21 23:25:11'),
('T39BEA3695A', 'R02624110', 'cashier', NULL, 135.00, 0.00, 0.00, 151.20, 0.00, 0.00, 'Cash', 160.00, 8.80, '2025-05-21 23:25:22', 'Completed', NULL, '2025-05-21 23:25:22', '2025-05-21 23:25:22'),
('TA4858B1645', 'R04014522', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 20.00, 0.00, 0.00, 22.40, 2.00, 0.00, 'Cash', 30.00, 7.60, '2025-05-21 23:25:45', 'Completed', NULL, '2025-05-21 23:25:45', '2025-05-21 23:25:45'),
('T258271FF58', 'R04600151', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 130.00, 0.00, 0.00, 145.60, 14.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 23:30:33', 'Completed', NULL, '2025-05-21 23:30:33', '2025-05-21 23:30:33'),
('T906ACE5329', 'R00034904', 'cashier', NULL, 130.00, 0.00, 0.00, 145.60, 0.00, 0.00, 'Cash', 150.00, 4.40, '2025-05-21 23:32:00', 'Completed', NULL, '2025-05-21 23:32:00', '2025-05-21 23:32:00'),
('T9019A32E20', 'R08502935', 'cashier', NULL, 30.00, 0.00, 0.00, 33.60, 0.00, 0.00, 'Cash', 40.00, 6.40, '2025-05-21 23:33:14', 'Completed', NULL, '2025-05-21 23:33:14', '2025-05-21 23:33:14'),
('TD948845A8C', 'R03631986', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 90.00, 0.00, 0.00, 100.80, 10.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-21 23:38:38', 'Completed', NULL, '2025-05-21 23:38:38', '2025-05-21 23:38:38'),
('TE6408C8498', 'R09019661', 'cashier', NULL, 90.00, 0.00, 0.00, 100.80, 0.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-21 23:43:51', 'Completed', NULL, '2025-05-21 23:43:51', '2025-05-21 23:43:51'),
('T7BF547C596', 'R06637598', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 135.00, 0.00, 0.00, 151.20, 15.00, 0.00, 'Cash', 160.00, 8.80, '2025-05-21 23:44:17', 'Completed', NULL, '2025-05-21 23:44:17', '2025-05-21 23:44:17'),
('T27EF5E34F3', 'R02940499', 'cashier', NULL, 90.00, 0.00, 0.00, 100.80, 0.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-21 23:47:04', 'Completed', NULL, '2025-05-21 23:47:04', '2025-05-21 23:47:04'),
('T8AEFCB868F', 'R04425341', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 90.00, 0.00, 0.00, 100.80, 10.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-21 23:47:21', 'Completed', NULL, '2025-05-21 23:47:21', '2025-05-21 23:47:21'),
('T66F2BC82E7', 'R06586664', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 51.00, 0.60, '2025-05-21 23:51:38', 'Completed', NULL, '2025-05-21 23:51:38', '2025-05-21 23:51:38'),
('T22E77B9609', 'R08835397', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 51.00, 0.60, '2025-05-21 23:56:37', 'Completed', NULL, '2025-05-21 23:56:37', '2025-05-21 23:56:37'),
('T61CB93B965', 'R03022327', 'cashier', NULL, 20.00, 0.00, 0.00, 22.40, 0.00, 0.00, 'Cash', 30.00, 7.60, '2025-05-21 23:58:47', 'Completed', NULL, '2025-05-21 23:58:47', '2025-05-21 23:58:47'),
('T6C14250934', 'R06936056', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 45.00, 0.00, 0.00, 50.40, 5.00, 0.00, 'Cash', 51.00, 0.60, '2025-05-21 23:59:10', 'Completed', NULL, '2025-05-21 23:59:10', '2025-05-21 23:59:10'),
('TACD3C57475', 'R09200106', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 90.00, 0.00, 0.00, 100.80, 10.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-22 00:01:58', 'Completed', NULL, '2025-05-22 00:01:58', '2025-05-22 00:01:58'),
('T2E9FA57D6C', 'R06111420', 'cashier', NULL, 10.00, 0.00, 0.00, 11.20, 0.00, 0.00, 'Cash', 12.00, 0.80, '2025-05-22 00:02:19', 'Completed', NULL, '2025-05-22 00:02:19', '2025-05-22 00:02:19'),
('T0D5FAA1518', 'R06782195', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 50.40, 0.00, '2025-05-22 00:06:42', 'Completed', NULL, '2025-05-22 00:06:42', '2025-05-22 00:06:42'),
('T0FA5C30771', 'R07434024', 'cashier', NULL, 65.00, 0.00, 0.00, 72.80, 0.00, 0.00, 'Cash', 80.00, 7.20, '2025-05-22 00:07:03', 'Completed', NULL, '2025-05-22 00:07:03', '2025-05-22 00:07:03'),
('TAC98CD04BC', 'R03576109', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 20.00, 0.00, 0.00, 22.40, 2.00, 0.00, 'Cash', 30.00, 7.60, '2025-05-22 00:11:17', 'Completed', NULL, '2025-05-22 00:11:17', '2025-05-22 00:11:17'),
('T1BE0DD3986', 'R08004847', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 51.00, 0.60, '2025-05-22 00:22:30', 'Completed', NULL, '2025-05-22 00:22:30', '2025-05-22 00:22:30'),
('T5A679F0FDC', 'R00305500', 'cashier', '4623aac7-10a5-427a-9fe2-38b029b49118', 90.00, 0.00, 0.00, 100.80, 10.00, 0.00, 'Cash', 110.00, 9.20, '2025-05-22 00:23:13', 'Completed', NULL, '2025-05-22 00:23:13', '2025-05-22 00:23:13'),
('T33A68CF72D', 'R06323200', 'cashier', 'fef7077c-b488-4f90-a407-9c7718cc1dba', 2000.00, 0.00, 0.00, 2240.00, 224.00, 0.00, 'Cash', 2500.00, 260.00, '2025-05-22 00:29:45', 'Completed', NULL, '2025-05-22 00:29:45', '2025-05-22 00:29:45'),
('T93DC5C39DD', 'R06009427', 'cashier', 'fef7077c-b488-4f90-a407-9c7718cc1dba', 9.64, 0.00, 0.00, 10.80, 1.00, 224.00, 'Cash', 11.00, 0.20, '2025-05-22 00:30:20', 'Completed', NULL, '2025-05-22 00:30:20', '2025-05-22 00:30:20'),
('TDFA13A25D9', 'R06540379', 'cashier', NULL, 45.00, 0.00, 0.00, 50.40, 0.00, 0.00, 'Cash', 51.00, 0.60, '2025-05-22 00:34:04', 'Completed', NULL, '2025-05-22 00:34:04', '2025-05-22 00:34:04'),
('TEABEE21A30', 'R04284660', 'cashier', NULL, 30.00, 0.00, 0.00, 33.60, 0.00, 0.00, 'Cash', 35.00, 1.40, '2025-05-22 00:34:31', 'Completed', NULL, '2025-05-22 00:34:31', '2025-05-22 00:34:31'),
('T8298795273', 'R03486776', 'cashier', NULL, 60.00, 0.00, 0.00, 67.20, 0.00, 0.00, 'Cash', 70.00, 2.80, '2025-05-22 00:37:37', 'Completed', NULL, '2025-05-22 00:37:37', '2025-05-22 00:37:37'),
('T0EF110FDD9', 'R05896037', 'cashier', 'fef7077c-b488-4f90-a407-9c7718cc1dba', 70.00, 0.00, 0.00, 78.40, 7.00, 0.00, 'Cash', 80.00, 1.60, '2025-05-22 00:38:03', 'Completed', NULL, '2025-05-22 00:38:03', '2025-05-22 00:38:03'),
('T1FB845CED2', 'R01603219', 'kahitano', NULL, 1075.00, 0.00, 0.00, 1204.00, 0.00, 0.00, 'Cash', 1500.00, 296.00, '2025-05-22 02:05:39', 'Completed', NULL, '2025-05-22 02:05:39', '2025-05-22 02:05:39'),
('T6EC9EB5F3D', 'R09469162', 'kahitano', '4623aac7-10a5-427a-9fe2-38b029b49118', 15.89, 0.00, 0.00, 17.80, 1.00, 111.00, 'Cash', 50.00, 32.20, '2025-05-22 02:08:39', 'Completed', NULL, '2025-05-22 02:08:39', '2025-05-22 02:08:39'),
('T3CD2B50869', 'R09524871', 'cashier', '9c7b7fb8-7d6d-4076-b3cd-5994553473fe', 39996.00, 0.00, 0.00, 44795.52, 4479.00, 0.00, 'Cash', 44795.52, 0.00, '2025-05-23 04:39:52', 'Completed', NULL, '2025-05-23 04:39:52', '2025-05-23 04:39:52'),
('TDAFDC97C18', 'R07399351', 'cashier', '9c7b7fb8-7d6d-4076-b3cd-5994553473fe', 15998.89, 0.00, 0.00, 17918.76, 1791.00, 4479.00, 'Cash', 17918.76, 0.00, '2025-05-23 04:41:50', 'Completed', NULL, '2025-05-23 04:41:50', '2025-05-23 04:41:50');

-- --------------------------------------------------------

--
-- Table structure for table `transaction_items`
--

DROP TABLE IF EXISTS `transaction_items`;
CREATE TABLE IF NOT EXISTS `transaction_items` (
  `id` varchar(36) NOT NULL,
  `transaction_id` varchar(36) NOT NULL,
  `item_id` varchar(36) NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `discount_amount` decimal(10,2) DEFAULT '0.00',
  `total_amount` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`),
  KEY `idx_transaction_items_transaction` (`transaction_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transaction_items`
--

INSERT INTO `transaction_items` (`id`, `transaction_id`, `item_id`, `quantity`, `unit_price`, `discount_amount`, `total_amount`, `created_at`) VALUES
('TI001', 'T001', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 04:09:59'),
('TI002', 'T001', 'I003', 1, 150.00, 0.00, 150.00, '2025-05-21 04:09:59'),
('TI003', 'T002', 'I002', 3, 65.00, 5.00, 190.00, '2025-05-21 04:09:59'),
('73b5472d-a600-482d-8a3c-ac3a3fa949ff', 'TD17356D3A6', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 05:04:58'),
('382e0e8a-2968-4f25-879d-69c172f4be6c', 'TA004BFBF4F', 'I003', 1, 150.00, 0.00, 150.00, '2025-05-21 05:12:30'),
('f53cfef4-f6af-4418-84bd-4da3249ea419', 'T3AC42864BA', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 05:14:59'),
('ebf3b460-8405-4581-b4ab-0ea3d02090d9', 'TEC0C6C93E2', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 05:16:42'),
('f818867b-78b9-4701-8dc5-0cd8f91069eb', 'T9C4C6AC898', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 05:20:42'),
('fd5d2e8a-565d-4aab-a4b0-4d0a1ab262c8', 'T03560C841E', 'I003', 1, 125.00, 0.00, 125.00, '2025-05-21 14:13:30'),
('66c6a746-4796-4a30-bb91-dbefd180a1be', 'TDC82123430', 'I003', 4, 125.00, 0.00, 500.00, '2025-05-21 14:22:02'),
('25806dd3-cd97-40da-adbd-9d6e28e030e4', 'T64E8C9ED77', 'I003', 1, 125.00, 0.00, 125.00, '2025-05-21 14:24:30'),
('266bad2c-4a64-4980-8475-418adc7c7cfd', 'T0401E1D8ED', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 15:15:17'),
('2eed5bb1-c165-4735-8d69-da2b0fd9d66a', 'TB39B891A8A', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 15:20:42'),
('a78c6378-60b3-42ee-a9de-5fd18d8f35b8', 'T3907EA0B21', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 15:20:55'),
('TI0011', 'T001', 'I001', 2, 20.00, 0.00, 40.00, '2025-05-21 15:35:52'),
('64634a54-315c-4e3e-8628-e93f4130177c', 'TB1FF87CA9C', 'I003', 1, 125.00, 0.00, 125.00, '2025-05-21 18:30:20'),
('7068817b-cec1-4f96-885d-d6b3612f0699', 'T0ED51F5ABA', 'I003', 1, 125.00, 0.00, 125.00, '2025-05-21 19:04:03'),
('4420b34e-c27f-418f-8228-63dc39809f33', 'T090E9AF76C', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:14:44'),
('a28d228f-8831-4796-9873-e63d731ae591', 'T2335730385', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:16:53'),
('9f31e72b-5537-4e78-a643-323cc95e63c4', 'TCB120C682C', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:19:43'),
('9f36c78b-e4f7-42b4-8769-585c216914a1', 'T4453AF6BFE', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:21:27'),
('ab3921bb-256d-4f08-9b55-e609ef275929', 'T7635FADD42', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:22:54'),
('27bd79d8-a83f-4fd9-bed4-be15983c1e5b', 'T69EBB841E3', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:30:54'),
('2f3e3fab-0987-4d54-981e-bfcaf113db98', 'TAD756C061C', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 19:32:31'),
('4734a321-fe0f-453d-8b99-102d856916e9', 'T759DF43D81', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:33:53'),
('994aca46-6441-43d4-babd-9935ee827c1c', 'T128F5B45D2', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 19:35:22'),
('08c3ffa8-c18a-4f91-8040-48ec20f0cc1c', 'T594D6933B6', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 19:36:09'),
('3853ecb8-6892-4059-936a-af9cbe879b1d', 'TB6349AE99B', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 19:37:52'),
('b02842b4-8712-433c-ad97-a001007acb69', 'TB6349AE99B', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 19:37:52'),
('f8a6d2de-6138-438b-b9a5-620c9d626821', 'TB6349AE99B', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 2, 250.00, 0.00, 500.00, '2025-05-21 19:37:52'),
('208c3905-d0a8-438f-afb3-c51da8b35167', 'T43BC922D5E', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 19:39:13'),
('b09e7640-3bef-43f8-b711-f534988f829e', 'T43BC922D5E', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 19:39:13'),
('adee77bc-ac68-40b1-97f7-2f13eef4bd16', 'T43BC922D5E', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 19:39:13'),
('c1c64230-7ce2-4780-bbf5-4c3e8c82e353', 'T1C0B507827', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 19:42:03'),
('b105e4d4-82fa-48c5-aefb-f73d86caf7b6', 'T1C0B507827', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 19:42:03'),
('a2d541f3-798a-43e5-84da-d59d3835147a', 'T1C0B507827', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 19:42:03'),
('f5812735-54ca-4957-a1f2-6a3b82cc9795', 'T1B024EB410', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 19:44:44'),
('9e5b79a2-cbaa-4ec7-b6b8-2f65a8dffe68', 'T1B024EB410', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 1, 250.00, 0.00, 250.00, '2025-05-21 19:44:44'),
('795cc38c-515b-411c-b898-f1241414ef13', 'T8817929874', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 19:45:54'),
('ca7b26a7-1f8f-468a-81e1-5412ba73cd1d', 'T80B1ABB173', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 19:48:24'),
('b2ebce28-eb2e-42ad-aac3-cf66bff7fbf0', 'T664B229AA1', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 19:54:17'),
('a2404b10-a614-4322-9afb-f1f2bbfa1b98', 'TE99E8717A3', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 19:58:05'),
('959e0145-d389-4831-9902-bc139f7a855b', 'TC4CD637080', 'ed383ce2-3309-4703-8232-2a35a6686578', 4, 10.00, 0.00, 40.00, '2025-05-21 20:04:14'),
('bf53c1d9-e2d9-4d7a-b364-dab3ce443071', 'TA59090DEB9', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 20:09:43'),
('b0ea9e0d-6512-4b38-9a61-4add79f06d27', 'TA96B23BD78', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 2, 250.00, 0.00, 500.00, '2025-05-21 20:14:19'),
('75fb992b-3bf3-45c1-b318-3c645221cf17', 'T2EDC6EC5D9', 'ed383ce2-3309-4703-8232-2a35a6686578', 4, 10.00, 0.00, 40.00, '2025-05-21 20:16:48'),
('4a90eebd-f617-45e4-b088-34ec2d5eae66', 'T2EDC6EC5D9', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 20:16:48'),
('19164b22-6788-41f9-82ee-24fd5d5eaa17', 'T2EDC6EC5D9', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 20:16:48'),
('75c539e4-8111-4ff7-bffc-e5e5a70dd016', 'TB997AE120B', 'I003', 2, 125.00, 0.00, 250.00, '2025-05-21 20:22:28'),
('eec8c9ad-d128-4be8-9772-44142551a63f', 'TDD725D1E2D', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 20:23:22'),
('8483d344-3664-4afb-88d2-46927b940791', 'TDD725D1E2D', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 20:23:22'),
('462008cb-f73d-4b3f-8438-b9a9cb23a1ba', 'T496A667ECA', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 20:24:28'),
('f27df8bc-f1b8-406f-8116-8b82c0e129c4', 'T496A667ECA', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 20:24:28'),
('d362c08d-b8a0-4cca-8aee-0b9341c18f85', 'T894D6E54C2', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 20:27:37'),
('faad6919-edbf-438c-89fe-57a1ed8d28d5', 'T894D6E54C2', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 20:27:37'),
('4f8b0989-d162-4826-b921-0bc918d1cf92', 'T894D6E54C2', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 20:27:37'),
('1c887b33-7b12-4404-929f-4886aa4de723', 'TE655EBB2D8', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 20:36:12'),
('4d7fdda9-5019-47c4-833c-e3120db31956', 'TE655EBB2D8', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 20:36:12'),
('b31d8177-1487-439f-857a-cda15327ca9d', 'TE655EBB2D8', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 20:36:12'),
('3b2ec692-68d6-4a95-ac84-c798e7d953ed', 'T89C498E342', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 20:39:56'),
('a098faeb-c6d4-4267-905e-4980f7a5d465', 'T89C498E342', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 1, 250.00, 0.00, 250.00, '2025-05-21 20:39:56'),
('6de1b39c-efbd-4a71-a1b7-449ea0bd946b', 'T89C498E342', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 20:39:56'),
('0f36dee6-c300-4cf4-9ed8-a916053b5e76', 'T89C498E342', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 20:39:56'),
('2b8d6f50-8271-4c86-af0a-ebeed4037698', 'T5C922D1959', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 20:42:43'),
('61fc8291-1d40-4f43-a34e-fcab1904eb8a', 'T5C922D1959', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-21 20:42:43'),
('c953c5d3-76a1-40a1-9a4e-011a63d7131e', 'T5C922D1959', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-21 20:42:43'),
('2d63c3d2-3a31-466c-a26d-133602804dc6', 'T5C922D1959', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 1, 250.00, 0.00, 250.00, '2025-05-21 20:42:43'),
('123a71ce-39ab-4475-b548-4aa4cac0e0ef', 'T3DA30FFFC0', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 23:06:31'),
('6301a1ec-b4bd-4970-8150-54f00d420006', 'TCAA1A32279', 'ed383ce2-3309-4703-8232-2a35a6686578', 3, 10.00, 0.00, 30.00, '2025-05-21 23:09:33'),
('ee9b0e66-1e5f-41dd-8789-ed0141ae9a2e', 'T3C9B58809F', 'ed383ce2-3309-4703-8232-2a35a6686578', 3, 10.00, 0.00, 30.00, '2025-05-21 23:12:25'),
('4a5abba4-be8e-4331-bce9-227a590f2932', 'T715FEA7AD5', 'ed383ce2-3309-4703-8232-2a35a6686578', 3, 10.00, 0.00, 30.00, '2025-05-21 23:12:29'),
('abff6ae6-997e-4658-bd91-16967b877157', 'T01C4E2B7B8', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 23:13:33'),
('8fdfc92b-de66-4a33-b494-44bcdb6c2b65', 'T61CF7E35BE', 'ed383ce2-3309-4703-8232-2a35a6686578', 5, 10.00, 0.00, 50.00, '2025-05-21 23:13:52'),
('8a9a5446-9264-4399-98bd-46520b292728', 'T0F9A30AC0B', 'ed383ce2-3309-4703-8232-2a35a6686578', 5, 10.00, 0.00, 50.00, '2025-05-21 23:16:17'),
('2fa35c96-3863-49f4-9d2a-8787575adc2c', 'T1FA264D1E8', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 23:16:57'),
('b74fa565-5436-474a-ab24-a932edaa7292', 'TE99B255803', 'I001', 3, 45.00, 0.00, 135.00, '2025-05-21 23:25:11'),
('4cd0482e-cb3a-4adf-b27f-342af8256d8c', 'T39BEA3695A', 'I001', 3, 45.00, 0.00, 135.00, '2025-05-21 23:25:22'),
('749086ef-ab95-40f0-af8f-6619f261b5c1', 'TA4858B1645', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 23:25:45'),
('6137172b-3070-4537-a4e2-ea4bf55afd6c', 'T258271FF58', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 23:30:33'),
('3d55ed69-e6e0-4efb-98e6-a8a954d1263d', 'T906ACE5329', 'I002', 2, 65.00, 0.00, 130.00, '2025-05-21 23:32:00'),
('a9e6cf23-daf4-4041-bcea-967c95b31d7a', 'T9019A32E20', 'ed383ce2-3309-4703-8232-2a35a6686578', 3, 10.00, 0.00, 30.00, '2025-05-21 23:33:14'),
('a7a3ab01-7bc4-49bb-8a7d-9920862bcf49', 'TD948845A8C', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 23:38:38'),
('0ef02679-b896-4939-957f-7610d22a4216', 'TE6408C8498', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 23:43:51'),
('ffa084e3-c74c-49c7-ae0e-2eb02cf166d5', 'T7BF547C596', 'I001', 3, 45.00, 0.00, 135.00, '2025-05-21 23:44:17'),
('51f21f56-14e6-473d-95a3-50dd1aa614a2', 'T27EF5E34F3', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 23:47:04'),
('17530446-33a7-4995-9aae-e20c97af9534', 'T8AEFCB868F', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-21 23:47:21'),
('6fe71357-889d-4e77-a71b-6cfafe6c5664', 'T66F2BC82E7', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 23:51:38'),
('24b840a8-b28b-4d9d-94ac-2373cf7c0e82', 'T22E77B9609', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 23:56:37'),
('7f791568-8b0a-4297-98a9-f29fe301764a', 'T3A2AC347E6', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-21 23:58:47'),
('2d560735-ef9e-41c4-bbbf-4416dc45ebea', 'T71C35CA272', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-21 23:59:10'),
('ce39dbbd-10d0-49b5-a607-1d6de8bceb1a', 'TACD3C57475', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-22 00:01:58'),
('eb421818-332a-4d4c-97f6-57e7b71120a5', 'T2E9FA57D6C', 'ed383ce2-3309-4703-8232-2a35a6686578', 1, 10.00, 0.00, 10.00, '2025-05-22 00:02:19'),
('c490a6ea-29de-481e-94e3-983cf5f66c04', 'TB977328648', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-22 00:06:42'),
('9084b69a-287b-4042-85f7-50740e3e2525', 'TE49063632F', 'I002', 1, 65.00, 0.00, 65.00, '2025-05-22 00:07:03'),
('7be70964-dd82-46a3-abc8-45c78dc73564', 'TAC98CD04BC', 'ed383ce2-3309-4703-8232-2a35a6686578', 2, 10.00, 0.00, 20.00, '2025-05-22 00:11:17'),
('98f67924-8032-4985-b90a-f5634d94d767', 'T1BE0DD3986', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-22 00:22:30'),
('05aa06d7-80f9-4786-b8a7-2abf1fed6f82', 'T5A679F0FDC', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-22 00:23:13'),
('9d5a38d8-6179-40ed-8abe-be8e4bf2965c', 'T33A68CF72D', 'feb27ca3-3d13-46e1-bfec-cc6ccc481245', 8, 250.00, 0.00, 2000.00, '2025-05-22 00:29:45'),
('d5bf774c-1ebd-4002-8e51-46982e869366', 'T93DC5C39DD', 'I001', 2, 45.00, 0.00, 90.00, '2025-05-22 00:30:20'),
('5296b090-2ab2-4de9-b3bd-c366a47f636b', 'TDFA13A25D9', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-22 00:34:04'),
('2b18df22-6503-441c-8a22-58a85bdd91bb', 'TEABEE21A30', 'ed383ce2-3309-4703-8232-2a35a6686578', 3, 10.00, 0.00, 30.00, '2025-05-22 00:34:31'),
('a8661559-93ec-4d43-8be7-fa292031a8cd', 'T8298795273', 'ed383ce2-3309-4703-8232-2a35a6686578', 6, 10.00, 0.00, 60.00, '2025-05-22 00:37:37'),
('36b0893f-77f0-4ea6-9e87-6815e1cbffa3', 'T0EF110FDD9', 'ed383ce2-3309-4703-8232-2a35a6686578', 7, 10.00, 0.00, 70.00, '2025-05-22 00:38:03'),
('6cdb05ae-a890-4709-bc95-a93cfc7dce01', 'T1FB845CED2', 'b8b96f5e-4d15-4686-9fe6-8c191e60c490', 50, 14.00, 0.00, 700.00, '2025-05-22 02:05:39'),
('bbb66325-fe38-4f5a-a798-2b2435efcae7', 'T1FB845CED2', 'I003', 3, 125.00, 0.00, 375.00, '2025-05-22 02:05:39'),
('78d36d3e-a560-4cd0-88ee-342f5a5e30cb', 'T6EC9EB5F3D', 'b8b96f5e-4d15-4686-9fe6-8c191e60c490', 5, 14.00, 0.00, 70.00, '2025-05-22 02:08:39'),
('ffab04dc-ef6a-4270-9f5b-a6317147fd4f', 'T6EC9EB5F3D', 'I001', 1, 45.00, 0.00, 45.00, '2025-05-22 02:08:39'),
('c6f41aca-251b-45a9-a941-7875f3696837', 'T3CD2B50869', '83e9b8ef-4d54-48ee-9c1a-1a21b44552a9', 4, 9999.00, 0.00, 39996.00, '2025-05-23 04:39:52'),
('3903b1f2-d2bd-4ba3-aad3-d29a253075f9', 'TDAFDC97C18', '83e9b8ef-4d54-48ee-9c1a-1a21b44552a9', 2, 9999.00, 0.00, 19998.00, '2025-05-23 04:41:50');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role` enum('Admin','Cashier') NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `last_login` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password_hash`, `full_name`, `role`, `email`, `active`, `last_login`, `created_at`, `updated_at`) VALUES
('1', 'admin', '$2a$12$7HNY4VuP7jSN/KabNwoknuh/3YdbH1VgE2/GbCsVzstWs4RV54q4u', 'System Administrator', 'Admin', 'admin@pos.com', 1, NULL, '2025-05-21 03:59:07', '2025-05-21 04:53:34'),
('2', 'cashier', '$2a$12$E24SNC5rrbr7npYhMXD08Osw5xIdMjwIap68Z5CbQAoXWSjQomWES', 'Default Cashier', 'Cashier', 'cashier@pos.com', 1, NULL, '2025-05-21 03:59:07', '2025-05-21 06:12:26'),
('7f057190-dca3-43a0-88ec-c48951c4c76d', 'cashier1', '$2a$12$EKbZrmHDRB.5XWzk99IPeeXtNfiNSE0nKmoeFt4kfSkMZz08/DKIO', 'cashier mila', 'Cashier', 'cashier1@gmail.com', 1, NULL, '2025-05-21 04:57:07', '2025-05-21 04:57:07'),
('517cf8a2-1afe-4701-bab2-0829bcef1cb7', 'cashier2', '$2a$12$LrCHFqt2sX5K1GGyJAqbduSkgEVTQ63BGzoNrQbQ4j5cl/ldSdn5S', 'cashier2', 'Cashier', 'cashier2@gmail.com', 1, NULL, '2025-05-21 05:58:28', '2025-05-21 05:58:28'),
('c83cba09-73cd-4c68-bb00-848d56b401f9', 'kahitano', '$2a$12$dg8hhOrTGahZFqdtgdire.4bDoqomrH.r3hDgYKuLNdtS.AMl7cZW', 'kahitano', 'Cashier', 'kahitano@yahoo.com', 1, NULL, '2025-05-22 02:04:00', '2025-05-22 02:04:00');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
