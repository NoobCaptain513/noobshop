-- 为订单状态并发更新增加乐观锁版本号
ALTER TABLE `order`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号' AFTER `update_time`;
