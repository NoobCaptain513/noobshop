-- ===========================================
-- V2__Add_product_stock_field.sql
-- Flyway 迁移脚本示例
-- 版本: 2
-- 描述: 为商品表添加库存相关字段
-- ===========================================

-- 示例：添加库存相关字段（根据项目实际情况修改）
-- ALTER TABLE `product` ADD COLUMN `stock_quantity` INT DEFAULT 0 COMMENT '库存数量' AFTER `price`;

-- 示例：创建索引
-- CREATE INDEX `idx_product_stock` ON `product` (`stock_quantity`);

-- 插入迁移记录
INSERT INTO `schema_version_log` (`version`, `description`, `script`, `installed_by`)
VALUES ('2', 'Add product stock field', 'V2__Add_product_stock_field.sql', CURRENT_USER());
