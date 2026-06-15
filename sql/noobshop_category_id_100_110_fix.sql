-- NoobShop category_id normalization.
-- Checked against the VM database noobshop on 192.168.100.135.
--
-- Useful category-id usages in this project:
--   1. product.category_id
--   2. banner.link_url when banner.link_type = 'category'
--   3. coupon_scope_detail.target_id when scope_type = 2
--
-- product_image, product_spec, cart, order_item and comments reference product_id,
-- so they do not need category_id updates.

SET NAMES utf8mb4;

START TRANSACTION;

-- 1. Keep category IDs in the 100-110 range.
-- The frontend already hardcodes the "精选" tab, so category 100 is reserved
-- and disabled to avoid showing a duplicate "居家日用" tab.
INSERT INTO category (id, name, parent_id, icon_url, sort, status, create_time, update_time)
VALUES
    (100, '预留分类', 0, '/static/images/default-category.png', 0, 0, NOW(), NOW()),
    (101, '居家日用', 0, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (102, '数码配件', 0, '/static/images/default-category.png', 2, 1, NOW(), NOW()),
    (103, '家居收纳', 0, '/static/images/default-category.png', 3, 1, NOW(), NOW()),
    (104, '食品饮料', 0, '/static/images/default-category.png', 4, 1, NOW(), NOW()),
    (105, '户外水具', 0, '/static/images/default-category.png', 5, 1, NOW(), NOW()),
    (106, '美妆个护', 0, '/static/images/default-category.png', 6, 1, NOW(), NOW()),
    (107, '办公文具', 0, '/static/images/default-category.png', 7, 1, NOW(), NOW()),
    (108, '厨房用品', 0, '/static/images/default-category.png', 8, 1, NOW(), NOW()),
    (109, '玩具模型', 0, '/static/images/default-category.png', 9, 1, NOW(), NOW()),
    (110, '服饰配饰', 0, '/static/images/default-category.png', 10, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    parent_id = VALUES(parent_id),
    icon_url = VALUES(icon_url),
    sort = VALUES(sort),
    status = VALUES(status),
    update_time = NOW();

-- 2. Recalculate product.category_id from product names.
-- This also fixes legacy IDs found in the VM data:
--   201/202 -> 102 数码配件
--   301     -> 104 食品饮料
UPDATE product
SET category_id = CASE
    WHEN name REGEXP '手机壳|保护套|手机保护壳|超薄透明壳|全包防摔壳|数据线|充电头|无线耳机|移动电源|读卡器|蓝牙音箱|Type-C|快充|鼠标|键盘' THEN 102
    WHEN name REGEXP '收纳盒|储物篮|杯垫|挂钩|桌面整理架' THEN 103
    WHEN name REGEXP '宠物零食|每日坚果|曲奇|饼干|饮料|食品|坚果' THEN 104
    WHEN name REGEXP '折叠水壶|运动水壶|水壶|骑行头盔|健身手套|跑步手套|遮阳帽|棒球帽|瑜伽垫' THEN 105
    WHEN name REGEXP '护手霜|卸妆水|面膜|口红|保湿乳|防晒霜' THEN 106
    WHEN name REGEXP '便利贴|记事本|文件夹|荧光笔|钢笔|桌面台历' THEN 107
    WHEN name REGEXP '密封罐|饺子模具|铲|保鲜盒|切菜板|隔热手套' THEN 108
    WHEN name REGEXP '手办模型|积木套装|益智玩具|遥控车|拼图|魔方|猫玩具' THEN 109
    WHEN name REGEXP '丝巾|钱包|发夹|帆布袋' THEN 110
    WHEN name REGEXP '防滑垫|狗绳|宠物梳|宠物窝|猫粮盆|宠物用品' THEN 101
    WHEN category_id IN (200, 201, 202) THEN 102
    WHEN category_id IN (300, 301) THEN 104
    WHEN category_id BETWEEN 100 AND 110 THEN category_id
    ELSE 101
END
WHERE status <> 0;

-- 3. Fix category banners if later data uses category link URLs.
UPDATE banner
SET link_url = CASE
    WHEN CAST(link_url AS UNSIGNED) IN (200, 201, 202) THEN '102'
    WHEN CAST(link_url AS UNSIGNED) IN (300, 301) THEN '104'
    WHEN CAST(link_url AS UNSIGNED) NOT BETWEEN 100 AND 110 THEN '101'
    ELSE link_url
END
WHERE link_type = 'category'
  AND link_url REGEXP '^[0-9]+$';

-- 4. Fix coupon category scopes if later data has category-scoped coupons.
UPDATE coupon_scope_detail
SET target_id = CASE
    WHEN target_id IN (200, 201, 202) THEN 102
    WHEN target_id IN (300, 301) THEN 104
    WHEN target_id NOT BETWEEN 100 AND 110 THEN 101
    ELSE target_id
END
WHERE scope_type = 2;

COMMIT;

-- 5. Verification queries.
SELECT
    c.id AS category_id,
    c.name AS category_name,
    c.status,
    COUNT(p.id) AS product_count
FROM category c
LEFT JOIN product p ON p.category_id = c.id AND p.status <> 0
WHERE c.id BETWEEN 100 AND 110
GROUP BY c.id, c.name, c.status
ORDER BY c.id;

SELECT
    'product_out_of_range' AS check_name,
    COUNT(*) AS bad_count
FROM product
WHERE status <> 0
  AND category_id NOT BETWEEN 100 AND 110;

SELECT
    'banner_category_out_of_range' AS check_name,
    COUNT(*) AS bad_count
FROM banner
WHERE link_type = 'category'
  AND link_url REGEXP '^[0-9]+$'
  AND CAST(link_url AS UNSIGNED) NOT BETWEEN 100 AND 110;

SELECT
    'coupon_category_out_of_range' AS check_name,
    COUNT(*) AS bad_count
FROM coupon_scope_detail
WHERE scope_type = 2
  AND target_id NOT BETWEEN 100 AND 110;
