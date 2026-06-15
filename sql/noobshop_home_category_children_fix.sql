-- NoobShop homepage category children fix.
--
-- Why this is needed:
-- The homepage calls /api/product/categroy/list with isFirstCategoryId=true.
-- In that mode the backend searches products by child category ids, not by the
-- clicked root category id itself. Root categories without children therefore
-- show an empty product list even when product.category_id has data.
--
-- This script creates one child category for each leaf root category currently
-- used on the homepage, then moves products from the root category to that new
-- child category.
--
-- After running this SQL:
--   1. Restart the backend, or otherwise refresh the category tree Redis/Caffeine cache.
--   2. Run com.app.noobshop.application.es.sync.ProductDataSyncApplication
--      because homepage category product lists are read from Elasticsearch.
--   3. Clear frontend category/product cache if the page still shows old data.

SET NAMES utf8mb4;

START TRANSACTION;

-- 1. Create child categories for root categories that currently have products
-- directly attached but no children.
INSERT INTO category (id, name, parent_id, icon_url, sort, status, create_time, update_time)
VALUES
    (1031, '全部家居收纳', 103, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1041, '全部食品饮料', 104, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1051, '全部户外水具', 105, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1061, '全部美妆个护', 106, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1071, '全部办公文具', 107, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1081, '全部厨房用品', 108, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1091, '全部玩具模型', 109, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (1101, '全部服饰配饰', 110, '/static/images/default-category.png', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    parent_id = VALUES(parent_id),
    icon_url = VALUES(icon_url),
    sort = VALUES(sort),
    status = VALUES(status),
    update_time = NOW();

-- 2. Move products from leaf root categories to the newly created children.
-- This makes the existing frontend request isFirstCategoryId=true work for all
-- homepage root categories.
UPDATE product
SET category_id = CASE category_id
    WHEN 103 THEN 1031
    WHEN 104 THEN 1041
    WHEN 105 THEN 1051
    WHEN 106 THEN 1061
    WHEN 107 THEN 1071
    WHEN 108 THEN 1081
    WHEN 109 THEN 1091
    WHEN 110 THEN 1101
    ELSE category_id
END
WHERE status <> 0
  AND category_id IN (103, 104, 105, 106, 107, 108, 109, 110);

COMMIT;

-- 3. Verification: every active root category should now have at least one
-- child category.
SELECT
    root.id AS root_category_id,
    root.name AS root_category_name,
    COUNT(child.id) AS child_count
FROM category root
LEFT JOIN category child
    ON child.parent_id = root.id
    AND child.status = 1
WHERE root.parent_id = 0
  AND root.status = 1
GROUP BY root.id, root.name
ORDER BY root.sort, root.id;

-- 4. Verification: product counts are now attached to child categories.
SELECT
    root.id AS root_category_id,
    root.name AS root_category_name,
    child.id AS child_category_id,
    child.name AS child_category_name,
    COUNT(p.id) AS product_count
FROM category root
JOIN category child
    ON child.parent_id = root.id
    AND child.status = 1
LEFT JOIN product p
    ON p.category_id = child.id
    AND p.status <> 0
WHERE root.parent_id = 0
  AND root.status = 1
GROUP BY root.id, root.name, child.id, child.name
ORDER BY root.sort, root.id, child.sort, child.id;

-- 5. Verification: these homepage leaf roots should no longer directly hold
-- active products.
SELECT
    category_id,
    COUNT(*) AS active_product_count
FROM product
WHERE status <> 0
  AND category_id IN (103, 104, 105, 106, 107, 108, 109, 110)
GROUP BY category_id
ORDER BY category_id;
