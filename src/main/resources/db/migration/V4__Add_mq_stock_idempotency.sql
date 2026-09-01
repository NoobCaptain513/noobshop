CREATE TABLE IF NOT EXISTS `mq_consume_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `change_id` VARCHAR(64) NOT NULL COMMENT '库存变更唯一标识',
    `order_no` VARCHAR(64) NULL COMMENT '关联订单号',
    `product_id` BIGINT NOT NULL,
    `spec_id` BIGINT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_change_id` (`change_id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MQ库存变更消费幂等记录';

ALTER TABLE `mq_consumer_failed_msg`
    ADD COLUMN `next_retry_time` DATETIME NULL COMMENT '下次补偿时间' AFTER `retry_count`,
    ADD INDEX `idx_failed_msg_retry` (`status`, `next_retry_time`, `id`);

UPDATE `mq_consumer_failed_msg`
SET `status` = COALESCE(`status`, 0),
    `retry_count` = COALESCE(`retry_count`, 0);

-- 旧版本保存的是描述性文本而不是 JSON，无法自动重放，保留记录供人工核对。
UPDATE `mq_consumer_failed_msg`
SET `status` = 2,
    `error_msg` = CONCAT(COALESCE(`error_msg`, ''), '; 历史消息体非JSON，需人工处理')
WHERE `status` = 0 AND (`body` IS NULL OR JSON_VALID(`body`) = 0);

UPDATE `mq_consumer_failed_msg`
SET `next_retry_time` = NOW()
WHERE `status` = 0 AND `next_retry_time` IS NULL;
