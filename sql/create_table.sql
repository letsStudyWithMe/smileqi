# 建表脚本
# @author smileqi
# @from smileqi

-- 创建库
CREATE DATABASE IF NOT EXISTS smileqi;

-- 切换库
USE smileqi;

-- 菜单权限表
CREATE TABLE IF NOT EXISTS `sys_menu`
(
    `id`          BIGINT                                                 NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name`        VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
    `parent_id`   BIGINT                                                  DEFAULT '0' COMMENT '父菜单ID',
    `order_num`   INT                                                     DEFAULT '0' COMMENT '显示顺序',
    `path`        VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '路由地址',
    `status`      CHAR(1) CHARACTER SET utf8 COLLATE utf8_general_ci      DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权限标识',
    `icon`        VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '#' COMMENT '菜单图标',
    `create_by`   VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME                                                DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME                                                DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = INNODB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='菜单权限表';

--
