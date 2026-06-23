/*
 Navicat Premium Dump SQL

 Source Server         : mysql5
 Source Server Type    : MySQL
 Source Server Version : 50720 (5.7.20)
 Source Schema         : zxb

 Target Server Type    : MySQL
 Target Server Version : 50720 (5.7.20)
 File Encoding         : 65001

 Date: 23/06/2026 15:54:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bak_company_info_cqq_20250205
-- ----------------------------
DROP TABLE IF EXISTS `bak_company_info_cqq_20250205`;
CREATE TABLE `bak_company_info_cqq_20250205`  (
                                                  `企业名称` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                  `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `登记状态` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `企业规模` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `注册资本` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `电话` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `更多电话` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `统一社会信用代码` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `法定代表人` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `成立日期` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `所属省份` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `所属城市` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `所属区县` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `参保人数` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `曾用名` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `user` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                  `status` int(1) NULL DEFAULT NULL COMMENT '0：不符合的企业；1：符合的企业',
                                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                  PRIMARY KEY (`企业名称`) USING BTREE,
                                                  UNIQUE INDEX `index_apply_id`(`apply_id`) USING BTREE,
                                                  INDEX `index_status`(`status`, `user`, `电话`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent`;
CREATE TABLE `bak_patent`  (
                               `patent_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `status` int(1) NULL DEFAULT 0 COMMENT '0：未获取数据，1：已获取未格式化，2：已格式化',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                               `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `apply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `apply_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `pub_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `legal_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `case_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `main_category_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`patent_id`) USING BTREE,
                               INDEX `index_case_status`(`case_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_apply
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_apply`;
CREATE TABLE `bak_patent_apply`  (
                                     `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `apply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `status` int(1) NULL DEFAULT NULL COMMENT '0：未联系，1：已联系不卖，2：已联系考虑卖，3：联系不上',
                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`apply_id`) USING BTREE,
                                     UNIQUE INDEX `index_apply_name`(`apply_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_apply_contact
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_apply_contact`;
CREATE TABLE `bak_patent_apply_contact`  (
                                             `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                             `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_apply_detail_20250205
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_apply_detail_20250205`;
CREATE TABLE `bak_patent_apply_detail_20250205`  (
                                                     `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                     `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `apply_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `pub_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `auth_notice_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `legal_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `case_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `main_category_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                     `status` int(1) NULL DEFAULT NULL COMMENT '默认0：其他；1：水利；2：建筑等',
                                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                     PRIMARY KEY (`patent_id`) USING BTREE,
                                                     INDEX `index_status`(`status`) USING BTREE,
                                                     INDEX `index_create`(`create_time`) USING BTREE,
                                                     INDEX `index_id_type_case_date`(`apply_id`, `type`, `case_status`, `apply_date`) USING BTREE,
                                                     INDEX `index_type_case_date`(`type`, `apply_date`, `case_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_content
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_content`;
CREATE TABLE `bak_patent_content`  (
                                       `patent_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `status` int(1) NULL DEFAULT 0 COMMENT '0：未获取数据，1：已获取未格式化，2：已格式化',
                                       `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                       `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`patent_id`) USING BTREE,
                                       INDEX `index_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_detail
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_detail`;
CREATE TABLE `bak_patent_detail`  (
                                      `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `apply_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `pub_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `legal_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `case_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `main_category_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `status` int(1) NULL DEFAULT NULL COMMENT '0：未处理apply和apply_detail数据，1：已处理',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`patent_id`) USING BTREE,
                                      INDEX `index_case_status`(`case_status`) USING BTREE,
                                      INDEX `index_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bak_patent_pdf
-- ----------------------------
DROP TABLE IF EXISTS `bak_patent_pdf`;
CREATE TABLE `bak_patent_pdf`  (
                                   `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                   `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`patent_id`) USING BTREE,
                                   INDEX `index_status`(`status`) USING BTREE,
                                   INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                   INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
                         `id` int(11) NOT NULL,
                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `publisher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `isbn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for child
-- ----------------------------
DROP TABLE IF EXISTS `child`;
CREATE TABLE `child`  (
                          `id` int(11) NOT NULL,
                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `identity_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `birthday` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `identity_num_left` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `identity_num_right` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `temp_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `temp_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `house` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          `class_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `index_temp_id_name`(`temp_name`, `temp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for company_info_cqq
-- ----------------------------
DROP TABLE IF EXISTS `company_info_cqq`;
CREATE TABLE `company_info_cqq`  (
                                     `企业名称` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `登记状态` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `企业规模` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `注册资本` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `电话` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `更多电话` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `统一社会信用代码` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `法定代表人` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `成立日期` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `所属省份` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `所属城市` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `所属区县` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `参保人数` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `曾用名` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `user` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `status` int(1) NULL DEFAULT NULL COMMENT '0：不符合的企业；1：符合的企业',
                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`企业名称`) USING BTREE,
                                     UNIQUE INDEX `index_apply_id`(`apply_id`) USING BTREE,
                                     INDEX `index_status`(`status`, `user`, `电话`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for company_property
-- ----------------------------
DROP TABLE IF EXISTS `company_property`;
CREATE TABLE `company_property`  (
                                     `id` int(11) NOT NULL,
                                     `zhuti` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `zichanbianhao` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `shebeimingcheng` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `pinpai` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `xinghao` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `shuliang` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `danjia` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `jine` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract`  (
                             `id` int(11) NOT NULL,
                             `party` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主体名称',
                             `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合同类型',
                             `number` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合同编号',
                             `counter_party` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对方单位',
                             `start_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合同有效期起',
                             `end_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合同有效期止',
                             `status` int(11) NULL DEFAULT NULL COMMENT '1已提醒 0未提醒',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
                             `id` int(11) NOT NULL,
                             `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `start_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `end_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `status` int(1) NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for equity_index_dividend_yield_daily
-- ----------------------------
DROP TABLE IF EXISTS `equity_index_dividend_yield_daily`;
CREATE TABLE `equity_index_dividend_yield_daily`  (
                                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                                      `equity_index` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指数代码',
                                                      `equity_index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指数代码名称',
                                                      `date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                      `dividend_yield` float NULL DEFAULT NULL COMMENT '股息率',
                                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      UNIQUE INDEX `uk_equity_index_date`(`equity_index`, `date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4361 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_name
-- ----------------------------
DROP TABLE IF EXISTS `file_name`;
CREATE TABLE `file_name`  (
                              `id` int(11) NOT NULL,
                              `ori_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                              `new_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fund_nav
-- ----------------------------
DROP TABLE IF EXISTS `fund_nav`;
CREATE TABLE `fund_nav`  (
                             `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                             `fund_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '基金代码',
                             `nav_date` date NOT NULL COMMENT '净值日期(交易日)',
                             `unit_nav` decimal(10, 4) NULL DEFAULT NULL COMMENT '单位净值',
                             `acc_nav` decimal(10, 4) NULL DEFAULT NULL COMMENT '累计净值',
                             `growth_rate` decimal(6, 4) NULL DEFAULT NULL COMMENT '日增长率(%,存小数：0.5代表0.5%)',
                             `subscribe_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '申购状态：开放/暂停',
                             `redeem_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '赎回状态：开放/暂停',
                             `bonus` decimal(10, 4) NULL DEFAULT NULL COMMENT '分红送配信息',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_fund_date`(`fund_code`, `nav_date`) USING BTREE,
                             INDEX `idx_fund_code`(`fund_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 462 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公募基金每日净值表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for github_repository
-- ----------------------------
DROP TABLE IF EXISTS `github_repository`;
CREATE TABLE `github_repository`  (
                                      `repository_id` int(11) NOT NULL,
                                      `repository` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `last_tag_id` int(11) NULL DEFAULT NULL,
                                      `last_tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `published_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `status` int(255) NULL DEFAULT NULL COMMENT '1:监测；0:不监测',
                                      PRIMARY KEY (`repository_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for id_prefix_city
-- ----------------------------
DROP TABLE IF EXISTS `id_prefix_city`;
CREATE TABLE `id_prefix_city`  (
                                   `identity_num_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                   `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   PRIMARY KEY (`identity_num_prefix`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for iyuu_site
-- ----------------------------
DROP TABLE IF EXISTS `iyuu_site`;
CREATE TABLE `iyuu_site`  (
                              `id` int(11) NOT NULL COMMENT 'iyuu站点id',
                              `site` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点英文名称',
                              `nickname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点昵称',
                              `base_url` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'url',
                              `download_page` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下载地址',
                              `reseed_check` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '辅种检验方式？passkey；cookie',
                              `is_https` int(11) NULL DEFAULT NULL COMMENT '0:？；1:？；2:？',
                              `supported` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'iyuu是否支持 1:是；0：否',
                              `uid` int(11) NULL DEFAULT NULL,
                              `passkey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                              `download_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                              `track_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'track域名',
                              `registered` int(255) NULL DEFAULT NULL COMMENT '是否已注册 1:是；0：否',
                              `important_level` int(255) NULL DEFAULT NULL COMMENT '1:重点；2:一般；3:忽略',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for iyuu_torrent
-- ----------------------------
DROP TABLE IF EXISTS `iyuu_torrent`;
CREATE TABLE `iyuu_torrent`  (
                                 `info_hash` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `sid` int(11) NULL DEFAULT NULL,
                                 `torrent_id` int(11) NULL DEFAULT NULL,
                                 PRIMARY KEY (`info_hash`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kaxin_company
-- ----------------------------
DROP TABLE IF EXISTS `kaxin_company`;
CREATE TABLE `kaxin_company`  (
                                  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `phone_index` int(11) NULL DEFAULT NULL,
                                  `apply_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for land
-- ----------------------------
DROP TABLE IF EXISTS `land`;
CREATE TABLE `land`  (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `date` date NULL DEFAULT NULL COMMENT '出让时间',
                         `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市名称',
                         `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域名称',
                         `land_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地块编号',
                         `land_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地块名称',
                         `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '39：已成交；40：未成交；41：流拍；42：拍卖转挂牌；43：延期；46：终止',
                         `acreage` float(11, 0) NULL DEFAULT NULL COMMENT '出让面积(平米)',
  `land_usage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用途',
  `volumetric_rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '容积率',
  `deal_price` float(11, 0) NULL DEFAULT NULL COMMENT '成交价(万元)',
  `build_price` float(11, 0) NULL DEFAULT NULL COMMENT '楼面价(元/m2)',
  `premium_rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '溢价率',
  `owner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '竞得单位',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '竞配情况',
  `detail_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地块详情地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_date`(`date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14627002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lottery_house
-- ----------------------------
DROP TABLE IF EXISTS `lottery_house`;
CREATE TABLE `lottery_house`  (
                                  `house_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                  `house_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '摇号楼盘名称',
                                  `total_people_num` int(11) NULL DEFAULT NULL COMMENT '总报名家庭数',
                                  `total_house_num` int(11) NULL DEFAULT NULL COMMENT '总房源数',
                                  `elite_people_num` int(11) NULL DEFAULT NULL COMMENT '人才家庭数',
                                  `elite_house_num` int(11) NULL DEFAULT NULL COMMENT '人才房源数',
                                  `elite_chance` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人才中签率',
                                  `homeless_people_num` int(11) NULL DEFAULT NULL COMMENT '无房家庭数',
                                  `homeless_house_num` int(11) NULL DEFAULT NULL COMMENT '无房房源数',
                                  `homeless_chance` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '无房家庭中签率',
                                  `unhomeless_people_num` int(11) NULL DEFAULT NULL COMMENT '有房家庭数',
                                  `unhomeless_house_num` int(11) NULL DEFAULT NULL COMMENT '有房房源数',
                                  `unhomeless_chance` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '有房家庭中签率',
                                  `status` int(11) NULL DEFAULT NULL,
                                  `lottery_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  PRIMARY KEY (`house_id`) USING BTREE,
                                  INDEX `index1`(`lottery_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for lottery_other_people
-- ----------------------------
DROP TABLE IF EXISTS `lottery_other_people`;
CREATE TABLE `lottery_other_people`  (
                                         `other_people_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                         `house_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `people_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                         `identity_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
                                         `record_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '查房编号',
                                         PRIMARY KEY (`other_people_id`) USING BTREE,
                                         INDEX `index_people_id`(`people_id`) USING BTREE,
                                         INDEX `index_name_id`(`name`, `identity_num`) USING BTREE,
                                         INDEX `index_id`(`identity_num`) USING BTREE,
                                         INDEX `index_house_id`(`house_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for lottery_people
-- ----------------------------
DROP TABLE IF EXISTS `lottery_people`;
CREATE TABLE `lottery_people`  (
                                   `people_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                   `serial_num` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登记编号',
                                   `house_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                   `identity_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
                                   `record_num` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '查房编号',
                                   `family_type` int(11) NULL DEFAULT NULL COMMENT '1 人才无房家庭，2 普通无房家庭，3 其他家庭',
                                   `has_other_people` int(11) NULL DEFAULT NULL COMMENT '是否有其他购房人',
                                   `lottery_rank` int(11) NULL DEFAULT NULL COMMENT '摇号序号',
                                   PRIMARY KEY (`people_id`) USING BTREE,
                                   INDEX `index_house_id`(`house_id`) USING BTREE,
                                   INDEX `index_name_id`(`name`, `identity_num`) USING BTREE,
                                   INDEX `index_house_id_lottery_rank`(`house_id`, `lottery_rank`) USING BTREE,
                                   INDEX `index_house_id_serial_num`(`house_id`, `serial_num`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for new_house_hangzhou_detail
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_detail`;
CREATE TABLE `new_house_hangzhou_detail`  (
                                              `detail_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '房源id',
                                              `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '楼盘id',
                                              `presell_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预售证id',
                                              `build_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '幢',
                                              `unit_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单元',
                                              `room_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房号',
                                              `build_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建筑面积',
                                              `inner_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '套内面积',
                                              `area_rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '得房率',
                                              `record_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请备案单价',
                                              `fix_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装修标准单价',
                                              `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '总价',
                                              `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
                                              PRIMARY KEY (`detail_id`) USING BTREE,
                                              INDEX `index_house_id`(`house_id`) USING BTREE,
                                              INDEX `index_presell_id`(`presell_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_presell
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_presell`;
CREATE TABLE `new_house_hangzhou_presell`  (
                                               `presell_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预售证id',
                                               `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '楼盘id',
                                               `presell_no` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预售证编号',
                                               `price` double(10, 2) NULL DEFAULT NULL COMMENT '均价',
                                               `date` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核发日期',
                                               `total_house_num` int(11) NULL DEFAULT NULL COMMENT '可售套数',
                                               `limit_house_num` int(11) NULL DEFAULT NULL COMMENT '限制套数',
                                               `sold_house_num` int(11) NULL DEFAULT NULL COMMENT '已售套数',
                                               `update_status` int(255) NULL DEFAULT NULL COMMENT '状态 0待更新 1已更新',
                                               PRIMARY KEY (`presell_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_v2
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_v2`;
CREATE TABLE `new_house_hangzhou_v2`  (
                                          `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '楼盘id',
                                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备案名',
                                          `name_spread` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '推广名',
                                          `price` int(11) NULL DEFAULT NULL COMMENT '均价',
                                          `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物业类型',
                                          `plot_ratio` double NULL DEFAULT NULL COMMENT '容积率',
                                          `green_ratio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绿化率',
                                          `cover_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '占地面积',
                                          `bulid_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建筑面积',
                                          `bulid_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建筑形式',
                                          `total_house_num` int(11) NULL DEFAULT NULL COMMENT '总户数',
                                          `car_park_num` int(11) NULL DEFAULT NULL COMMENT '车位数',
                                          `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属区域',
                                          `area_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域编码',
                                          `plate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '板块',
                                          `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
                                          `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`house_id`) USING BTREE,
                                          INDEX `index_name`(`name`) USING BTREE,
                                          INDEX `index_name_spead`(`name_spread`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_v3
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_v3`;
CREATE TABLE `new_house_hangzhou_v3`  (
                                          `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `temp_house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `useful` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `company` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`house_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_v3_detail
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_v3_detail`;
CREATE TABLE `new_house_hangzhou_v3_detail`  (
                                                 `detail_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                 `build_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `presell_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `build_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `unit_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `room_no` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `build_area` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `inner_area` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `area_rate` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `unit_price` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `total_price` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                 `status` int(1) NULL DEFAULT NULL COMMENT '1：可售，2：已售，3：已经预定，4：限制房产',
                                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                 PRIMARY KEY (`detail_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_v3_presell
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_v3_presell`;
CREATE TABLE `new_house_hangzhou_v3_presell`  (
                                                  `presell_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                  `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                  `status` int(1) NULL DEFAULT NULL COMMENT '0:未处理，1:已处理',
                                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                  PRIMARY KEY (`presell_id`) USING BTREE,
                                                  INDEX `index_house_presell`(`house_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for new_house_hangzhou_v3_presell_build
-- ----------------------------
DROP TABLE IF EXISTS `new_house_hangzhou_v3_presell_build`;
CREATE TABLE `new_house_hangzhou_v3_presell_build`  (
                                                        `build_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                        `build_no` varchar(220) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                        `presell_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                        `house_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                        `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                        PRIMARY KEY (`build_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
                           `notice_id` int(11) NOT NULL AUTO_INCREMENT,
                           `notice_title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `notice_time` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'YYYY-MM-dd',
                           `notice_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多用户用|分开；空字符串为推送所有人',
                           `status` int(1) NULL DEFAULT NULL COMMENT '0:未推送，1:已推送',
                           PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 169 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_apply
-- ----------------------------
DROP TABLE IF EXISTS `patent_apply`;
CREATE TABLE `patent_apply`  (
                                 `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `apply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `patent_count` int(11) NULL DEFAULT NULL,
                                 `status` int(1) NULL DEFAULT NULL COMMENT '-3：未做费减；-1：总专利数太多；0：待联系；3：已成交',
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`apply_id`) USING BTREE,
                                 UNIQUE INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                 INDEX `index_status_name`(`status`, `apply_id`, `apply_name`, `create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_date
-- ----------------------------
DROP TABLE IF EXISTS `patent_date`;
CREATE TABLE `patent_date`  (
                                `apply_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `start_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `fee_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `end_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                PRIMARY KEY (`apply_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_detail
-- ----------------------------
DROP TABLE IF EXISTS `patent_detail`;
CREATE TABLE `patent_detail`  (
                                  `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `apply_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `pub_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `auth_notice_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `legal_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `case_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `main_category_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `status` int(1) NULL DEFAULT NULL COMMENT '默认0：其他；1：水利；2：建筑等',
                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`patent_id`) USING BTREE,
                                  INDEX `index_status`(`status`) USING BTREE,
                                  INDEX `index_create`(`create_time`) USING BTREE,
                                  INDEX `index_id_type_case_date`(`apply_id`, `type`, `case_status`, `apply_date`) USING BTREE,
                                  INDEX `index_type_case_date`(`type`, `apply_date`, `case_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_detail_doc_info
-- ----------------------------
DROP TABLE IF EXISTS `patent_detail_doc_info`;
CREATE TABLE `patent_detail_doc_info`  (
                                           `doc_info_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                           `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                           `doc_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知书名称',
                                           `doc_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发文日',
                                           `recipient` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人姓名',
                                           `postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人邮编',
                                           `download_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下载时间',
                                           `download_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下载IP地址',
                                           `doc_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发文方式',
                                           `index` int(2) NULL DEFAULT NULL COMMENT '顺序',
                                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`doc_info_id`) USING BTREE,
                                           INDEX `index_patent_id`(`patent_id`, `index`) USING BTREE,
                                           INDEX `index_doc_name_date`(`doc_name`, `doc_type`, `index`, `doc_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_detail_yitong
-- ----------------------------
DROP TABLE IF EXISTS `patent_detail_yitong`;
CREATE TABLE `patent_detail_yitong`  (
                                         `patent_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1000：7-1；0100：7-2；0010：7-3',
                                         `user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                         `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`patent_id`) USING BTREE,
                                         INDEX `index_user`(`user`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf`;
CREATE TABLE `patent_pdf`  (
                               `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                               `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`patent_id`) USING BTREE,
                               INDEX `index_status`(`status`) USING BTREE,
                               INDEX `index_apply_name`(`apply_name`) USING BTREE,
                               INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_2020
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_2020`;
CREATE TABLE `patent_pdf_2020`  (
                                    `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                    `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`patent_id`) USING BTREE,
                                    INDEX `index_status`(`status`) USING BTREE,
                                    INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                    INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_2021
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_2021`;
CREATE TABLE `patent_pdf_2021`  (
                                    `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                    `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`patent_id`) USING BTREE,
                                    INDEX `index_status`(`status`) USING BTREE,
                                    INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                    INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_2022
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_2022`;
CREATE TABLE `patent_pdf_2022`  (
                                    `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                    `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`patent_id`) USING BTREE,
                                    INDEX `index_status`(`status`) USING BTREE,
                                    INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                    INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_2023
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_2023`;
CREATE TABLE `patent_pdf_2023`  (
                                    `patent_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `auth_notice_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `main_category_num` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                    `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`patent_id`) USING BTREE,
                                    INDEX `index_status`(`status`) USING BTREE,
                                    INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                    INDEX `index_auth_date`(`auth_notice_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_apply
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_apply`;
CREATE TABLE `patent_pdf_apply`  (
                                     `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `apply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `patent_auth_count_2020` int(11) NULL DEFAULT NULL,
                                     `patent_auth_count_2021` int(11) NULL DEFAULT NULL,
                                     `patent_auth_count_2022` int(11) NULL DEFAULT NULL,
                                     `patent_auth_count_2023` int(11) NULL DEFAULT NULL,
                                     `patent_auth_count_2024` int(11) NULL DEFAULT NULL,
                                     `status` int(1) NULL DEFAULT NULL COMMENT '-4：国企、外企、名字不对；-3：未做费减；-2：总专利数太多；-1：不考虑处理；0：需要处理；1：已处理；3：已成交',
                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`apply_id`) USING BTREE,
                                     UNIQUE INDEX `index_apply_name`(`apply_name`) USING BTREE,
                                     INDEX `index_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_pdf_apply_temp
-- ----------------------------
DROP TABLE IF EXISTS `patent_pdf_apply_temp`;
CREATE TABLE `patent_pdf_apply_temp`  (
                                          `apply_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `apply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `patent_auth_count_2020` int(11) NULL DEFAULT NULL,
                                          `patent_auth_count_2021` int(11) NULL DEFAULT NULL,
                                          `patent_auth_count_2022` int(11) NULL DEFAULT NULL,
                                          `patent_auth_count_2023` int(11) NULL DEFAULT NULL,
                                          `patent_auth_count_2024` int(11) NULL DEFAULT NULL,
                                          `status` int(1) NULL DEFAULT NULL COMMENT '-1：不考虑处理；0：需要处理；1：已处理',
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`apply_id`) USING BTREE,
                                          UNIQUE INDEX `index_apply_name`(`apply_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_temp
-- ----------------------------
DROP TABLE IF EXISTS `patent_temp`;
CREATE TABLE `patent_temp`  (
                                `temp1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `temp2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `temp3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                INDEX `temp1`(`temp1`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patent_trade
-- ----------------------------
DROP TABLE IF EXISTS `patent_trade`;
CREATE TABLE `patent_trade`  (
                                 `patent_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `from_apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `to_apply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`patent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for property
-- ----------------------------
DROP TABLE IF EXISTS `property`;
CREATE TABLE `property`  (
                             `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                             `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cookie,device_id',
                             `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `value2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `value3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1可用，0不可用',
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qywx_chatgpt_msg
-- ----------------------------
DROP TABLE IF EXISTS `qywx_chatgpt_msg`;
CREATE TABLE `qywx_chatgpt_msg`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `from_user_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                     `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `prompt_tokens` int(11) NULL DEFAULT 0,
                                     `completion_tokens` int(11) NULL DEFAULT 0,
                                     `totalTokens` int(11) NULL DEFAULT 0,
                                     `create_time` datetime NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 456 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qywx_menu
-- ----------------------------
DROP TABLE IF EXISTS `qywx_menu`;
CREATE TABLE `qywx_menu`  (
                              `menu_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
                              `agent_id` int(11) NULL DEFAULT NULL,
                              `button` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一级菜单名称',
                              `button_level` int(11) NULL DEFAULT NULL COMMENT '菜单等级：一级、二级',
                              `has_sub_button` int(11) NULL DEFAULT NULL COMMENT '是否有子菜单',
                              `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单标题',
                              `type` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '类型：链接或者小程序',
                              `key` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '菜单地址：若是小程序，则保存小程序的page_path',
                              `order` int(11) NULL DEFAULT NULL COMMENT '展示顺序',
                              `status` int(11) NULL DEFAULT NULL COMMENT '1：上线；0、下线（没用的)',
                              PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '公众号菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qywx_patent_msg
-- ----------------------------
DROP TABLE IF EXISTS `qywx_patent_msg`;
CREATE TABLE `qywx_patent_msg`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `agent_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `from_user_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '01：报价单，02：合同协议',
                                    `apply_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `price` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报价单时是单价，合同协议时是总价',
                                    `mobile` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '00：接收成功；01：已生成；02：已推送',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1374 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for region
-- ----------------------------
DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`  (
                           `region_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                           `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `pinyin` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `level` varchar(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `parent_region_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                           `rec_create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `rec_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`region_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for second_hand_house
-- ----------------------------
DROP TABLE IF EXISTS `second_hand_house`;
CREATE TABLE `second_hand_house`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'gpfyid 挂牌房源id',
                                      `fwtybh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房源编号',
                                      `xzqhname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '市区名称',
                                      `cqmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域名称',
                                      `xqmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小区名称',
                                      `jzmj` float NULL DEFAULT NULL COMMENT '建筑面积',
                                      `wtcsjg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '委托出售价格',
                                      `mdmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '挂牌中介公司',
                                      `gplxrxm` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售姓名',
                                      `scgpshsj` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '挂牌时间',
                                      `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                                      `status_time` datetime NULL DEFAULT NULL COMMENT '状态更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `index1`(`fwtybh`, `id`) USING BTREE,
                                      INDEX `index2`(`xqmc`) USING BTREE,
                                      INDEX `index3`(`fwtybh`) USING BTREE,
                                      INDEX `index4`(`scgpshsj`) USING BTREE,
                                      INDEX `index5`(`status`, `status_time`) USING BTREE,
                                      INDEX `index6`(`xzqhname`, `cqmc`, `xqmc`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4556639 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for second_hand_house_before_2020
-- ----------------------------
DROP TABLE IF EXISTS `second_hand_house_before_2020`;
CREATE TABLE `second_hand_house_before_2020`  (
                                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'gpfyid 挂牌房源id',
                                                  `fwtybh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房源编号',
                                                  `xzqhname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '市区名称',
                                                  `cqmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域名称',
                                                  `xqmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小区名称',
                                                  `jzmj` float NULL DEFAULT NULL COMMENT '建筑面积',
                                                  `wtcsjg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '委托出售价格',
                                                  `mdmc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '挂牌中介公司',
                                                  `gplxrxm` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售姓名',
                                                  `scgpshsj` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '挂牌时间',
                                                  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                                                  `status_time` datetime NULL DEFAULT NULL COMMENT '状态更新时间',
                                                  PRIMARY KEY (`id`) USING BTREE,
                                                  INDEX `index1`(`fwtybh`, `id`) USING BTREE,
                                                  INDEX `index2`(`xqmc`) USING BTREE,
                                                  INDEX `index3`(`fwtybh`) USING BTREE,
                                                  INDEX `index4`(`scgpshsj`) USING BTREE,
                                                  INDEX `index5`(`status`, `status_time`) USING BTREE,
                                                  INDEX `index6`(`xzqhname`, `cqmc`, `xqmc`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26179661 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tech_company
-- ----------------------------
DROP TABLE IF EXISTS `tech_company`;
CREATE TABLE `tech_company`  (
                                 `company_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `year` int(11) NULL DEFAULT NULL,
                                 `index` int(11) NULL DEFAULT NULL,
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`company_id`) USING BTREE,
                                 INDEX `index_name`(`name`) USING BTREE,
                                 INDEX `index_year_index`(`year`, `index`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for temp_file
-- ----------------------------
DROP TABLE IF EXISTS `temp_file`;
CREATE TABLE `temp_file`  (
                              `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tiktok_account
-- ----------------------------
DROP TABLE IF EXISTS `tiktok_account`;
CREATE TABLE `tiktok_account`  (
                                   `account_id` int(11) NOT NULL,
                                   `country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `email` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `password` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `faction_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `status` int(1) NULL DEFAULT 0,
                                   `excel_start` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1.每日数据',
                                   `excel_end` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1.每日数据',
                                   `excel_monthly_index` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '2.每月预估激励',
                                   `excel_monthly_data_index` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '3.月度数据',
                                   `excel_monthly_data_partner` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '3.月度数据是否有伙伴',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tiktok_creator_data
-- ----------------------------
DROP TABLE IF EXISTS `tiktok_creator_data`;
CREATE TABLE `tiktok_creator_data`  (
                                        `user_id` int(11) NOT NULL AUTO_INCREMENT,
                                        `account_id` int(11) NULL DEFAULT NULL,
                                        `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        `rank` int(11) NULL DEFAULT NULL,
                                        `display_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        `income_current` int(11) NULL DEFAULT NULL,
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tiktok_data
-- ----------------------------
DROP TABLE IF EXISTS `tiktok_data`;
CREATE TABLE `tiktok_data`  (
                                `data_id` int(11) NOT NULL AUTO_INCREMENT,
                                `account_id` int(11) NULL DEFAULT NULL,
                                `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `today_diamond_cnt` int(11) NULL DEFAULT NULL,
                                `today_live_cnt` int(11) NULL DEFAULT NULL,
                                `today_new_member_cnt` int(11) NULL DEFAULT NULL,
                                `month_diamond_cnt` int(11) NULL DEFAULT NULL,
                                `month_diamond_pct` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                `month_new_creator_cnt` int(11) NULL DEFAULT NULL,
                                `month_new_creator_diamond_cnt` int(11) NULL DEFAULT NULL,
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tr_rss
-- ----------------------------
DROP TABLE IF EXISTS `tr_rss`;
CREATE TABLE `tr_rss`  (
                           `rss_id` int(11) NOT NULL,
                           `host_cn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `rss_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `pattern` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `common_download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           `status` int(11) NULL DEFAULT NULL COMMENT '0:停用；1:启用',
                           `interval_minutes` int(11) NULL DEFAULT NULL COMMENT '间隔时长',
                           PRIMARY KEY (`rss_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tr_rss_torrent
-- ----------------------------
DROP TABLE IF EXISTS `tr_rss_torrent`;
CREATE TABLE `tr_rss_torrent`  (
                                   `rss_torrent_id` int(11) NOT NULL,
                                   `rss_id` int(11) NULL DEFAULT NULL,
                                   `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                   `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `status` int(11) NULL DEFAULT NULL COMMENT '0:未处理；1:已推送下载；2:已跳过；3:失败',
                                   `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `pub_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`rss_torrent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tr_torrent
-- ----------------------------
DROP TABLE IF EXISTS `tr_torrent`;
CREATE TABLE `tr_torrent`  (
                               `id` int(20) NOT NULL,
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                               `status` int(11) NULL DEFAULT NULL COMMENT '0:已暂停;1:正在等待校验;2:正在校验;3:正在等待下载;4:下载中;5:等待做种;6:做种中',
                               `total_size` bigint(255) NULL DEFAULT NULL COMMENT '大小',
                               `download_dir` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '保存地址',
                               `error` int(20) NULL DEFAULT NULL COMMENT '错误',
                               `error_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
                               `hash_string` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '种子hash',
                               `upload_ratio` double NULL DEFAULT NULL COMMENT '分享率',
                               `percent_done` double NULL DEFAULT NULL COMMENT '完成进度',
                               `peers_sending_to_us` int(11) NULL DEFAULT NULL COMMENT '传输给我们的连接数',
                               `peers_getting_from_us` int(11) NULL DEFAULT NULL COMMENT '我们传输的连接数',
                               `rate_upload` int(11) NULL DEFAULT NULL COMMENT '已上传',
                               `added_date` int(11) NULL DEFAULT NULL COMMENT '添加时间',
                               `activity_date` int(11) NULL DEFAULT NULL COMMENT '最后活动时间',
                               `done_date` int(11) NULL DEFAULT NULL COMMENT '完成时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tr_torrent_host
-- ----------------------------
DROP TABLE IF EXISTS `tr_torrent_host`;
CREATE TABLE `tr_torrent_host`  (
                                    `id` int(11) NOT NULL,
                                    `host_cn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1:重点；2:一般；3:忽略',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tr_torrent_tracker
-- ----------------------------
DROP TABLE IF EXISTS `tr_torrent_tracker`;
CREATE TABLE `tr_torrent_tracker`  (
                                       `id` int(11) NOT NULL,
                                       `torrent_id` int(11) NULL DEFAULT NULL,
                                       `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '种子服务器',
                                       `seeder_count` int(11) NULL DEFAULT NULL COMMENT '正在做种数',
                                       `leecher_count` int(11) NULL DEFAULT NULL COMMENT '正在下载数',
                                       `announce_state` int(11) NULL DEFAULT NULL COMMENT '0:非活动;1:待机;2:队列中;3:活动中',
                                       `announce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `idnex_torrent_id`(`torrent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Procedure structure for splitPhoneNumbers
-- ----------------------------
DROP PROCEDURE IF EXISTS `splitPhoneNumbers`;
delimiter ;;
CREATE PROCEDURE `splitPhoneNumbers`(userName VARCHAR(1000))
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE company VARCHAR(255);
    DECLARE phones VARCHAR(255);
		DECLARE applyid VARCHAR(255);
    DECLARE phone VARCHAR(255);
    DECLARE idx INT;
    DECLARE cur CURSOR FOR SELECT `企业名称` as company,replace(replace(concat(`电话`,";",`更多电话`),";-",""),"-","") as phones,apply_id as applyid from company_info_cqq where user = userName;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
truncate kaxin_company;

OPEN cur;

read_loop: LOOP
        FETCH cur INTO company, phones, applyid;
        IF done THEN
            LEAVE read_loop;
END IF;

        SET idx = 1;
        WHILE CHAR_LENGTH(phones) > 0 DO
            SET phone = SUBSTRING_INDEX(phones, ';', 1);
            SET phones = SUBSTRING(phones, CHAR_LENGTH(phone) + 2);
INSERT INTO kaxin_company (company_name, phone_number, phone_index,apply_id) VALUES (company, phone, idx,applyid);
SET idx = idx + 1;
END WHILE;
END LOOP;

CLOSE cur;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for test
-- ----------------------------
DROP PROCEDURE IF EXISTS `test`;
delimiter ;;
CREATE PROCEDURE `test`(in_names VARCHAR(1000))
BEGIN

	-- DECLARE sql VARCHAR(10) DEFAULT '';

		SET @baseSql = "select * from new_house_hangzhou_v2
		where name in ";

		SET @sql = CONCAT(@baseSql,in_names);

prepare stmt1 from @sql;
execute stmt1;

END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for updatePatentPdfApply
-- ----------------------------
DROP PROCEDURE IF EXISTS `updatePatentPdfApply`;
delimiter ;;
CREATE PROCEDURE `updatePatentPdfApply`()
BEGIN
-- 每日执行一更新当天新的开始有滞纳金的数据（实际是超过1个月有滞纳金之后的4-5天了）

	SET @dateA = DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -35 DAY), '%m-%d');
	SET @dateB = DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 187 DAY), '%m-%d');
	SET @date1 = concat("2021-",@dateA);
	SET @date2 = concat("2022-",@dateA);
	SET @date3 = concat("2023-",@dateA);
  SET @date4 = concat("2024-",@dateA);
	SET @date5 = concat("2021-",@dateB);
	SET @date6 = concat("2022-",@dateB);
	SET @date7 = concat("2023-",@dateB);
  SET @date8 = concat("2024-",@dateB);


UPDATE patent_pdf_apply ppa
    JOIN (
    SELECT DISTINCT a.apply_id
    FROM patent_detail a
-- 			LEFT JOIN company_info_cqq cc ON a.apply_id = cc.apply_id
    LEFT JOIN tech_company tc ON a.apply_name = tc.`name`
    Left JOIN patent_pdf_apply ppa on a.apply_id=ppa.apply_id
    WHERE
    a.type in ("实用新型","发明专利")
    AND a.case_status IN ('专利权维持','等年费滞纳金','未缴年费专利权终止，等恢复','合议组审查','形式审查','等待形式审查分配')-- ,'驳回等复审请求','一通出案待答复'
    AND a.apply_date IN (@date1, @date2, @date3, @date4, @date5, @date6, @date7, @date8)
    AND a.status in (1,2)
    AND tc.company_id IS NULL
    -- -4：国企或者国外企业；-3：未做费减；-2：总专利数太多；-1：不考虑处理；0：需要处理；1：已处理；3：已成交
    -- AND ppa.status<>0
    AND ppa.status = 1
    AND a.create_time < DATE_SUB(CURDATE(), INTERVAL 7 DAY) + INTERVAL 86399 SECOND
    ) subquery
ON ppa.apply_id = subquery.apply_id
    SET ppa.status = 0;

END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
