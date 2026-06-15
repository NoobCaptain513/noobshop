-- NoobShop frontend/backend database compatibility fixes.
-- Recommended import order:
-- 数据库.sql -> product.sql -> product_image.sql -> noobshop_other_seed.sql -> noobshop_frontend_db_fix.sql
--
-- Run this file as a full SQL script. The DELIMITER lines are required.
--
-- Fixes:
-- 1. Add order_no to product_comment and product_comment_append for frontend comment flows.
-- 2. Create product_comment_like for comment like/unlike.
-- 3. Allow multiple logistics timeline rows for the same logistics_no.
-- 4. Ensure every product has at least one numeric product_spec row.

SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS noobshop_apply_frontend_db_fix;

DELIMITER $$

CREATE PROCEDURE noobshop_apply_frontend_db_fix()
BEGIN
    -- product_comment.order_no is required by ProductComment.orderNo and comment append logic.
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment'
          AND column_name = 'order_no'
    ) THEN
        ALTER TABLE product_comment
            ADD COLUMN order_no varchar(50) NULL COMMENT '订单编号（冗余，关联 order.order_no）' AFTER order_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment'
          AND index_name = 'idx_product_comment_order_no'
    ) THEN
        CREATE INDEX idx_product_comment_order_no ON product_comment (order_no);
    END IF;

    -- product_comment_append.order_no is required by ProductCommentAppend.orderNo.
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment_append'
          AND column_name = 'order_no'
    ) THEN
        ALTER TABLE product_comment_append
            ADD COLUMN order_no varchar(50) NULL COMMENT '订单编号（冗余，关联 order.order_no）' AFTER order_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment_append'
          AND index_name = 'idx_product_comment_append_order_no'
    ) THEN
        CREATE INDEX idx_product_comment_append_order_no ON product_comment_append (order_no);
    END IF;

    -- Comment like table used by ProductCommentLikeMapper.
    CREATE TABLE IF NOT EXISTS product_comment_like
    (
        id          bigint auto_increment comment '点赞记录ID（主键）'
            primary key,
        comment_id  bigint                             not null comment '评论ID',
        user_id     bigint                             not null comment '用户ID',
        status      tinyint  default 1                 not null comment '点赞状态：1-已点赞，0-已取消',
        create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
        update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
        constraint uk_comment_user
            unique (comment_id, user_id)
    )
        comment '商品评论点赞表' collate = utf8mb4_unicode_ci;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment_like'
          AND index_name = 'idx_product_comment_like_comment_id'
    ) THEN
        CREATE INDEX idx_product_comment_like_comment_id ON product_comment_like (comment_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'product_comment_like'
          AND index_name = 'idx_product_comment_like_user_id'
    ) THEN
        CREATE INDEX idx_product_comment_like_user_id ON product_comment_like (user_id);
    END IF;

    -- order_tracking needs multiple rows per logistics_no for the frontend timeline.
    IF EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'order_tracking'
          AND index_name = 'order_tracking_one'
    ) THEN
        ALTER TABLE order_tracking DROP INDEX order_tracking_one;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'order_tracking'
          AND index_name = 'idx_order_tracking_logistics_no'
    ) THEN
        CREATE INDEX idx_order_tracking_logistics_no ON order_tracking (logistics_no);
    END IF;
END$$

DELIMITER ;

CALL noobshop_apply_frontend_db_fix();

DROP PROCEDURE IF EXISTS noobshop_apply_frontend_db_fix;

-- Backfill order_no for existing seed comments that only had order_id.
UPDATE product_comment pc
JOIN `order` o ON pc.order_id = o.id
SET pc.order_no = o.order_no
WHERE pc.order_no IS NULL;

UPDATE product_comment_append pca
JOIN `order` o ON pca.order_id = o.id
SET pca.order_no = o.order_no
WHERE pca.order_no IS NULL;

-- Keep first comments consistent when an append comment already exists.
UPDATE product_comment pc
SET pc.is_append_comment = 1
WHERE EXISTS (
    SELECT 1
    FROM product_comment_append pca
    WHERE pca.comment_id = pc.id
);

INSERT INTO product_comment_like (comment_id, user_id, status, create_time, update_time)
VALUES
    (930001, 90002, 1, NOW(), NOW()),
    (930001, 90003, 1, NOW(), NOW()),
    (930002, 90004, 1, NOW(), NOW()),
    (930003, 90001, 1, NOW(), NOW()),
    (930004, 90005, 1, NOW(), NOW()),
    (930006, 90001, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    update_time = NOW();

INSERT INTO order_tracking
    (id, logistics_no, order_id, logistics_status, location, description, create_time)
VALUES
    (920004, 'SF202605280001', 900001, 1, '浙江省杭州市余杭区', '快件已由商家交付，等待揽收。', '2026-05-28 16:35:00'),
    (920005, 'SF202605280001', 900001, 2, '浙江省杭州市转运中心', '快件已到达杭州转运中心，准备发往目的地。', '2026-05-29 08:20:00'),
    (920006, 'SF202605280001', 900001, 3, '浙江省杭州市西湖区', '快件正在派送中，请保持电话畅通。', '2026-05-31 09:18:00'),
    (920007, 'ZTO202605290002', 900002, 1, '江苏省南京市仓配中心', '快件已揽收。', '2026-05-30 09:15:00'),
    (920008, 'ZTO202605290002', 900002, 2, '江苏省南京市建邺转运站', '快件运输中，即将到达派送网点。', '2026-05-31 18:40:00'),
    (920009, 'YS202606010003', 900003, 1, '广东省深圳市南山区', '商家已通知快递员上门揽收。', '2026-06-01 18:20:00')
ON DUPLICATE KEY UPDATE
    logistics_no = VALUES(logistics_no),
    order_id = VALUES(order_id),
    logistics_status = VALUES(logistics_status),
    location = VALUES(location),
    description = VALUES(description),
    create_time = VALUES(create_time);

-- Frontend creates nonnumeric fallback spec ids only when a product has no specList.
-- Give every product at least one numeric spec so cart/order flows can use real spec_id.
INSERT INTO product_spec
    (product_id, spec_text, price, enterprise_price, stock, create_time, update_time)
SELECT
    p.id,
    '默认规格',
    p.price,
    p.enterprise_price,
    GREATEST(p.stock, 0),
    NOW(),
    NOW()
FROM product p
WHERE NOT EXISTS (
    SELECT 1
    FROM product_spec ps
    WHERE ps.product_id = p.id
);

