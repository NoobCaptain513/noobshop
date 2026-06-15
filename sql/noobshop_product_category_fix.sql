-- NoobShop product category fix.
-- Align product.category_id with the homepage category names used by the frontend.
-- Run this after importing 数据库.sql, product.sql, product_image.sql and noobshop_other_seed.sql.

SET NAMES utf8mb4;

-- 1. Keep category names/order consistent with the homepage tabs.
-- "精选" is a fixed frontend tab and is not stored in the category table.
INSERT INTO category (id, name, parent_id, icon_url, sort, status, create_time, update_time)
VALUES
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

-- 2. Recalculate product.category_id by product name.
-- This is idempotent: rerunning the script keeps products in the same category.
UPDATE product
SET category_id = CASE
    WHEN name REGEXP '手机壳|保护套|手机保护壳|超薄透明壳|全包防摔壳|数据线|充电头|无线耳机|移动电源|读卡器|蓝牙音箱' THEN 102
    WHEN name REGEXP '收纳盒|储物篮|杯垫|挂钩|桌面整理架' THEN 103
    WHEN name REGEXP '宠物零食|每日坚果|曲奇|饼干|饮料|食品' THEN 104
    WHEN name REGEXP '折叠水壶|运动水壶|水壶|骑行头盔|健身手套|跑步手套|遮阳帽|棒球帽|瑜伽垫' THEN 105
    WHEN name REGEXP '护手霜|卸妆水|面膜|口红|保湿乳|防晒霜' THEN 106
    WHEN name REGEXP '便利贴|记事本|文件夹|荧光笔|钢笔|桌面台历' THEN 107
    WHEN name REGEXP '密封罐|饺子模具|铲|保鲜盒|切菜板|隔热手套' THEN 108
    WHEN name REGEXP '手办模型|积木套装|益智玩具|遥控车|拼图|魔方|猫玩具' THEN 109
    WHEN name REGEXP '丝巾|钱包|发夹|帆布袋' THEN 110
    WHEN name REGEXP '防滑垫|狗绳|宠物梳|宠物窝|猫粮盆|宠物用品' THEN 101
    ELSE category_id
END
WHERE status <> 0;

-- 3. Add category-oriented search keywords used by the frontend search entry.
INSERT INTO product_search_keyword (keyword, is_hot, is_show, create_time, update_time)
VALUES
    ('居家日用', 1, 1, NOW(), NOW()),
    ('数码配件', 1, 1, NOW(), NOW()),
    ('家居收纳', 1, 1, NOW(), NOW()),
    ('食品饮料', 1, 1, NOW(), NOW()),
    ('户外水具', 1, 1, NOW(), NOW()),
    ('美妆个护', 1, 1, NOW(), NOW()),
    ('办公文具', 1, 1, NOW(), NOW()),
    ('厨房用品', 1, 1, NOW(), NOW()),
    ('玩具模型', 1, 1, NOW(), NOW()),
    ('服饰配饰', 1, 1, NOW(), NOW()),
    ('蓝牙音箱', 1, 1, NOW(), NOW()),
    ('读卡器', 0, 1, NOW(), NOW()),
    ('防晒霜', 0, 1, NOW(), NOW()),
    ('钢笔', 0, 1, NOW(), NOW()),
    ('魔方', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    is_hot = VALUES(is_hot),
    is_show = VALUES(is_show),
    update_time = NOW();

-- 4. Quick check after execution.
SELECT
    c.id AS category_id,
    c.name AS category_name,
    COUNT(p.id) AS product_count
FROM category c
LEFT JOIN product p ON p.category_id = c.id AND p.status <> 0
WHERE c.id BETWEEN 101 AND 110
GROUP BY c.id, c.name
ORDER BY c.sort, c.id;
