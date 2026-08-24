-- ===========================================
-- V1__Initial_schema.sql
-- Flyway 初始迁移脚本示例
-- 版本: 1
-- 描述: 初始化数据库表结构
-- ===========================================

-- 创建示例表（可根据实际项目修改）
-- 注意：基线脚本，通常用于已有项目的首次迁移

-- 用户表扩表示例（如果原表已存在，此脚本仅作演示）
-- 实际使用时，请根据当前数据库结构编写

-- 示例：创建一个演示用的日志表
CREATE TABLE IF NOT EXISTS `schema_version_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `version` VARCHAR(20) NOT NULL COMMENT '版本号',
    `description` VARCHAR(200) COMMENT '描述',
    `script` VARCHAR(1000) COMMENT '执行的脚本',
    `installed_on` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '安装时间',
    `installed_by` VARCHAR(100) COMMENT '执行人',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Flyway 迁移日志表';

-- 插入当前版本记录
INSERT INTO `schema_version_log` (`version`, `description`, `script`, `installed_by`)
VALUES ('1', 'Initial schema baseline', 'V1__Initial_schema.sql', CURRENT_USER());
