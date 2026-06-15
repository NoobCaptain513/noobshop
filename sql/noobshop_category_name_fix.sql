-- NoobShop category names aligned with the frontend homepage.
-- "精选" is a fixed frontend tab and is not stored in category.

SET NAMES utf8mb4;

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

