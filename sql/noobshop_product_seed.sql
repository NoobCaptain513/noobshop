-- NoobShop product seed data for local development.
-- Run this against the noobshop MySQL database, then run:
-- com.app.noobshop.application.es.sync.ProductDataSyncApplication

SET NAMES utf8mb4;

INSERT INTO category (id, name, parent_id, icon_url, sort, status, create_time, update_time)
VALUES
    (100, '居家日用', 0, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (101, '收纳整理', 100, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (102, '厨房用品', 100, '/static/images/default-category.png', 2, 1, NOW(), NOW()),
    (200, '数码配件', 0, '/static/images/default-category.png', 2, 1, NOW(), NOW()),
    (201, '充电设备', 200, '/static/images/default-category.png', 1, 1, NOW(), NOW()),
    (202, '桌面外设', 200, '/static/images/default-category.png', 2, 1, NOW(), NOW()),
    (300, '食品饮料', 0, '/static/images/default-category.png', 3, 1, NOW(), NOW()),
    (301, '休闲零食', 300, '/static/images/default-category.png', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    parent_id = VALUES(parent_id),
    icon_url = VALUES(icon_url),
    sort = VALUES(sort),
    status = VALUES(status),
    update_time = NOW();

INSERT INTO product
    (id, category_id, name, sell_point, price, enterprise_price, stock, cover_image, description, view_count, sales_count, status, create_time, update_time)
VALUES
    (10001, 101, '菜鸟加厚透明收纳箱 45L', '大容量可叠放，衣物玩具一箱收好', 39.90, 35.90, 280,
     'https://dummyimage.com/800x800/e8f5e9/1b5e20.png&text=NoobShop+Storage+Box',
     '<p>加厚PP材质，透明箱体，卧室、客厅、宿舍均可使用。</p>', 1680, 482, 1, NOW(), NOW()),
    (10002, 101, '菜鸟桌面分格收纳盒', '多格分类，桌面小物不凌乱', 19.90, 16.90, 520,
     'https://dummyimage.com/800x800/e3f2fd/0d47a1.png&text=NoobShop+Organizer',
     '<p>适合文具、遥控器、化妆刷、数据线等小物分类收纳。</p>', 1432, 356, 1, NOW(), NOW()),
    (10003, 102, '菜鸟304不锈钢保温杯 500ml', '长效保温，通勤随身携带', 49.90, 43.90, 360,
     'https://dummyimage.com/800x800/fff3e0/e65100.png&text=NoobShop+Cup',
     '<p>食品接触级304不锈钢，杯盖密封，冷热饮均可使用。</p>', 2198, 731, 1, NOW(), NOW()),
    (10004, 102, '菜鸟厨房沥水置物架', '碗盘刀具分区摆放，台面更清爽', 69.90, 62.90, 170,
     'https://dummyimage.com/800x800/f3e5f5/4a148c.png&text=NoobShop+Kitchen+Rack',
     '<p>加粗碳钢结构，带接水盘，适合日常厨房收纳。</p>', 1205, 268, 1, NOW(), NOW()),
    (10005, 201, '菜鸟65W氮化镓快充充电器', '三口输出，手机平板电脑都能充', 89.00, 79.00, 210,
     'https://dummyimage.com/800x800/e0f7fa/006064.png&text=NoobShop+Charger',
     '<p>支持多协议快充，体积小，适合差旅办公。</p>', 3240, 980, 1, NOW(), NOW()),
    (10006, 201, '菜鸟Type-C快充数据线 1.5m', '加粗线芯，充电传输更稳定', 15.90, 12.90, 900,
     'https://dummyimage.com/800x800/fce4ec/880e4f.png&text=NoobShop+Cable',
     '<p>尼龙编织线身，耐弯折，适配Type-C接口设备。</p>', 2980, 1126, 1, NOW(), NOW()),
    (10007, 202, '菜鸟静音无线鼠标', '轻巧便携，办公学习低噪点击', 29.90, 25.90, 430,
     'https://dummyimage.com/800x800/ede7f6/311b92.png&text=NoobShop+Mouse',
     '<p>2.4G无线连接，静音微动，左右手均可舒适使用。</p>', 1766, 518, 1, NOW(), NOW()),
    (10008, 202, '菜鸟机械键盘 87键 茶轴', '紧凑布局，办公游戏两相宜', 129.00, 115.00, 140,
     'https://dummyimage.com/800x800/e8eaf6/1a237e.png&text=NoobShop+Keyboard',
     '<p>87键布局节省桌面空间，茶轴手感均衡，支持多媒体快捷键。</p>', 1544, 401, 1, NOW(), NOW()),
    (10009, 301, '菜鸟每日坚果 750g', '多种坚果果干搭配，独立小袋装', 59.90, 52.90, 260,
     'https://dummyimage.com/800x800/fff8e1/ff6f00.png&text=NoobShop+Nuts',
     '<p>每日一包，早餐、下午茶、出行补给都方便。</p>', 2468, 864, 1, NOW(), NOW()),
    (10010, 301, '菜鸟手工曲奇饼干礼盒', '酥脆香甜，送礼自用都合适', 45.90, 39.90, 190,
     'https://dummyimage.com/800x800/fbe9e7/bf360c.png&text=NoobShop+Cookie',
     '<p>多口味组合，独立包装，适合办公室零食和节日礼盒。</p>', 1188, 299, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    category_id = VALUES(category_id),
    name = VALUES(name),
    sell_point = VALUES(sell_point),
    price = VALUES(price),
    enterprise_price = VALUES(enterprise_price),
    stock = VALUES(stock),
    cover_image = VALUES(cover_image),
    description = VALUES(description),
    view_count = VALUES(view_count),
    sales_count = VALUES(sales_count),
    status = VALUES(status),
    update_time = NOW();

INSERT INTO product_spec
    (id, product_id, spec_text, price, enterprise_price, stock, create_time, update_time)
VALUES
    (20001, 10001, '45L 透明款', 39.90, 35.90, 180, NOW(), NOW()),
    (20002, 10001, '65L 透明款', 55.90, 49.90, 100, NOW(), NOW()),
    (20003, 10002, '三格款', 19.90, 16.90, 300, NOW(), NOW()),
    (20004, 10002, '六格款', 25.90, 22.90, 220, NOW(), NOW()),
    (20005, 10003, '白色 500ml', 49.90, 43.90, 180, NOW(), NOW()),
    (20006, 10003, '黑色 500ml', 49.90, 43.90, 180, NOW(), NOW()),
    (20007, 10004, '单层款', 69.90, 62.90, 100, NOW(), NOW()),
    (20008, 10004, '双层款', 99.90, 89.90, 70, NOW(), NOW()),
    (20009, 10005, '65W 三口 白色', 89.00, 79.00, 210, NOW(), NOW()),
    (20010, 10006, '1.5m 黑色', 15.90, 12.90, 500, NOW(), NOW()),
    (20011, 10006, '2m 黑色', 19.90, 16.90, 400, NOW(), NOW()),
    (20012, 10007, '白色', 29.90, 25.90, 230, NOW(), NOW()),
    (20013, 10007, '黑色', 29.90, 25.90, 200, NOW(), NOW()),
    (20014, 10008, '茶轴 白光', 129.00, 115.00, 140, NOW(), NOW()),
    (20015, 10009, '750g 30袋', 59.90, 52.90, 260, NOW(), NOW()),
    (20016, 10010, '礼盒装 600g', 45.90, 39.90, 190, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    spec_text = VALUES(spec_text),
    price = VALUES(price),
    enterprise_price = VALUES(enterprise_price),
    stock = VALUES(stock),
    update_time = NOW();

INSERT INTO product_image (id, product_id, image_url, sort, create_time)
VALUES
    (30001, 10001, 'https://dummyimage.com/800x800/e8f5e9/1b5e20.png&text=Storage+Box+1', 1, NOW()),
    (30002, 10001, 'https://dummyimage.com/800x800/f1f8e9/33691e.png&text=Storage+Box+2', 2, NOW()),
    (30003, 10002, 'https://dummyimage.com/800x800/e3f2fd/0d47a1.png&text=Organizer+1', 1, NOW()),
    (30004, 10003, 'https://dummyimage.com/800x800/fff3e0/e65100.png&text=Cup+1', 1, NOW()),
    (30005, 10004, 'https://dummyimage.com/800x800/f3e5f5/4a148c.png&text=Kitchen+Rack+1', 1, NOW()),
    (30006, 10005, 'https://dummyimage.com/800x800/e0f7fa/006064.png&text=Charger+1', 1, NOW()),
    (30007, 10006, 'https://dummyimage.com/800x800/fce4ec/880e4f.png&text=Cable+1', 1, NOW()),
    (30008, 10007, 'https://dummyimage.com/800x800/ede7f6/311b92.png&text=Mouse+1', 1, NOW()),
    (30009, 10008, 'https://dummyimage.com/800x800/e8eaf6/1a237e.png&text=Keyboard+1', 1, NOW()),
    (30010, 10009, 'https://dummyimage.com/800x800/fff8e1/ff6f00.png&text=Nuts+1', 1, NOW()),
    (30011, 10010, 'https://dummyimage.com/800x800/fbe9e7/bf360c.png&text=Cookie+1', 1, NOW())
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    image_url = VALUES(image_url),
    sort = VALUES(sort);

INSERT INTO product_search_keyword (id, keyword, is_hot, is_show, create_time, update_time)
VALUES
    (40001, '菜鸟收纳箱', 1, 1, NOW(), NOW()),
    (40002, '菜鸟快充', 1, 1, NOW(), NOW()),
    (40003, '菜鸟鼠标', 1, 1, NOW(), NOW()),
    (40004, '菜鸟坚果', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    keyword = VALUES(keyword),
    is_hot = VALUES(is_hot),
    is_show = VALUES(is_show),
    update_time = NOW();
