-- NoobShop non-product seed data.
-- Recommended import order: 数据库.sql -> product.sql -> product_image.sql -> noobshop_other_seed.sql
-- Skipped table marked unusable: `consult(unuseable)`.
SET NAMES utf8mb4;

INSERT INTO category
    (id, name, parent_id, icon_url, sort, status, create_time, update_time)
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
ON DUPLICATE KEY UPDATE name = VALUES(name), parent_id = VALUES(parent_id), icon_url = VALUES(icon_url), sort = VALUES(sort), status = VALUES(status), update_time = NOW();

INSERT INTO admin
    (id, username, password, nickname, role, last_login_time, last_login_ip, status, create_time, update_time)
VALUES
    (80001, 'seed_admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '演示管理员', 1, '2026-06-01 09:10:00', '127.0.0.1', 1, NOW(), NOW()),
    (80002, 'seed_operator', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '平台运营', 2, '2026-06-02 14:25:00', '127.0.0.1', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE password = VALUES(password), nickname = VALUES(nickname), role = VALUES(role), last_login_time = VALUES(last_login_time), last_login_ip = VALUES(last_login_ip), status = VALUES(status), update_time = NOW();

INSERT INTO sys_user
    (id, username, password, nickname, avatar, phone, openid, user_type, is_enable, first_login_time, last_login_time, create_time, update_time)
VALUES
    (90000, 'service_seed', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'NoobShop客服', '/static/images/default-avatar.png', '13800009000', NULL, 2, 1, '2026-05-01 09:00:00', '2026-06-02 18:10:00', NOW(), NOW()),
    (90001, 'buyer_chen', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '陈小满', '/static/images/default-avatar.png', '13800009001', NULL, 3, 1, '2026-05-12 10:20:00', '2026-06-03 12:15:00', NOW(), NOW()),
    (90002, 'buyer_lin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '林一诺', '/static/images/default-avatar.png', '13800009002', NULL, 3, 1, '2026-05-14 11:30:00', '2026-06-02 20:35:00', NOW(), NOW()),
    (90003, 'buyer_wang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王嘉禾', '/static/images/default-avatar.png', '13800009003', NULL, 3, 1, '2026-05-16 15:45:00', '2026-06-01 08:12:00', NOW(), NOW()),
    (90004, 'buyer_zhao', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵安然', '/static/images/default-avatar.png', '13800009004', NULL, 3, 1, '2026-05-20 13:05:00', '2026-06-03 09:21:00', NOW(), NOW()),
    (90005, 'buyer_sun', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙若溪', '/static/images/default-avatar.png', '13800009005', NULL, 3, 1, '2026-05-22 16:28:00', '2026-06-02 10:44:00', NOW(), NOW()),
    (90006, 'manager_luo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '罗店长', '/static/images/default-avatar.png', '13800009006', NULL, 1, 1, '2026-05-03 08:50:00', '2026-06-03 15:30:00', NOW(), NOW()),
    (90007, 'operator_he', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '何运营', '/static/images/default-avatar.png', '13800009007', NULL, 2, 1, '2026-05-06 09:12:00', '2026-06-03 11:05:00', NOW(), NOW()),
    (90008, 'buyer_zhou', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周清扬', '/static/images/default-avatar.png', '13800009008', NULL, 3, 1, '2026-05-24 10:18:00', '2026-06-02 21:13:00', NOW(), NOW()),
    (90009, 'buyer_wu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '吴知夏', '/static/images/default-avatar.png', '13800009009', NULL, 3, 1, '2026-05-25 14:42:00', '2026-06-01 17:22:00', NOW(), NOW()),
    (90010, 'buyer_xu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '徐明哲', '/static/images/default-avatar.png', '13800009010', NULL, 3, 1, '2026-05-26 09:33:00', '2026-06-03 08:18:00', NOW(), NOW()),
    (90011, 'buyer_hu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '胡星河', '/static/images/default-avatar.png', '13800009011', NULL, 3, 1, '2026-05-27 19:05:00', '2026-06-02 13:55:00', NOW(), NOW()),
    (90012, 'buyer_gao', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '高南乔', '/static/images/default-avatar.png', '13800009012', NULL, 3, 1, '2026-05-28 20:16:00', '2026-06-01 22:20:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE password = VALUES(password), nickname = VALUES(nickname), avatar = VALUES(avatar), phone = VALUES(phone), user_type = VALUES(user_type), is_enable = VALUES(is_enable), last_login_time = VALUES(last_login_time), update_time = NOW();

INSERT INTO sys_role
    (id, role_code, role_name, sort, is_enable, create_time)
VALUES
    (70001, 'ROLE_SUPER_ADMIN', '超级管理员', 1, 1, NOW()),
    (70002, 'ROLE_OPERATOR', '平台运营', 2, 1, NOW()),
    (70003, 'ROLE_CUSTOMER_SERVICE', '客服人员', 3, 1, NOW()),
    (70004, 'ROLE_BUYER', '普通买家', 4, 1, NOW())
ON DUPLICATE KEY UPDATE role_code = VALUES(role_code), role_name = VALUES(role_name), sort = VALUES(sort), is_enable = VALUES(is_enable);

INSERT INTO sys_permission
    (id, perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
VALUES
    (71001, 'dashboard:view', '查看控制台', 2, 0, 1, 1, NOW()),
    (71002, 'product:view', '查看商品', 2, 0, 2, 1, NOW()),
    (71003, 'product:edit', '编辑商品', 2, 0, 3, 1, NOW()),
    (71004, 'order:view', '查看订单', 2, 0, 4, 1, NOW()),
    (71005, 'order:deliver', '订单发货', 2, 0, 5, 1, NOW()),
    (71006, 'comment:audit', '审核评价', 2, 0, 6, 1, NOW()),
    (71007, 'feedback:reply', '回复反馈', 2, 0, 7, 1, NOW()),
    (71008, 'notice:manage', '维护公告', 2, 0, 8, 1, NOW()),
    (71009, 'user:view', '查看用户', 2, 0, 9, 1, NOW()),
    (71010, 'role:manage', '维护角色权限', 2, 0, 10, 1, NOW())
ON DUPLICATE KEY UPDATE perm_code = VALUES(perm_code), perm_name = VALUES(perm_name), perm_type = VALUES(perm_type), parent_id = VALUES(parent_id), sort = VALUES(sort), is_enable = VALUES(is_enable);

INSERT INTO sys_role_permission
    (id, role_id, perm_id)
VALUES
    (72001, 70001, 71001),
    (72002, 70001, 71002),
    (72003, 70001, 71003),
    (72004, 70001, 71004),
    (72005, 70001, 71005),
    (72006, 70001, 71006),
    (72007, 70001, 71007),
    (72008, 70001, 71008),
    (72009, 70001, 71009),
    (72010, 70001, 71010),
    (72011, 70002, 71001),
    (72012, 70002, 71002),
    (72013, 70002, 71003),
    (72014, 70002, 71004),
    (72015, 70002, 71005),
    (72016, 70002, 71006),
    (72017, 70002, 71008),
    (72018, 70003, 71004),
    (72019, 70003, 71007),
    (72020, 70003, 71009),
    (72021, 70004, 71002)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id), perm_id = VALUES(perm_id);

INSERT INTO sys_user_role
    (id, user_id, role_id)
VALUES
    (73001, 90000, 70003),
    (73002, 90001, 70004),
    (73003, 90002, 70004),
    (73004, 90003, 70004),
    (73005, 90004, 70004),
    (73006, 90005, 70004),
    (73007, 90006, 70001),
    (73008, 90007, 70002),
    (73009, 90008, 70004),
    (73010, 90009, 70004),
    (73011, 90010, 70004),
    (73012, 90011, 70004),
    (73013, 90012, 70004)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), role_id = VALUES(role_id);

INSERT INTO banner
    (id, title, image_url, link_url, link_type, sort, status, create_time, update_time)
VALUES
    (81001, '品牌TPU密封罐', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/A1KqYQhL6OL.jpg', '/product/detail?productId=10001', 'product', 1, 1, NOW(), NOW()),
    (81002, '原装TPU手机壳', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/A1dSzaZBLwL.jpg', '/product/detail?productId=10002', 'product', 2, 1, NOW(), NOW()),
    (81003, '热销亚克力杯垫', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/91Nar%2B4rtIL.jpg', '/product/detail?productId=10003', 'product', 3, 1, NOW(), NOW()),
    (81004, '热销TPU充电头', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/912IBVDWtnL.jpg', '/product/detail?productId=10007', 'product', 4, 1, NOW(), NOW()),
    (81005, '精选TPU宠物零食', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/910iQLeJ%2BJL.jpg', '/product/detail?productId=10009', 'product', 5, 1, NOW(), NOW()),
    (81006, '网红TPU护手霜', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/916lYWLdbYL.jpg', '/product/detail?productId=10020', 'product', 6, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), image_url = VALUES(image_url), link_url = VALUES(link_url), link_type = VALUES(link_type), sort = VALUES(sort), status = VALUES(status), update_time = NOW();

INSERT INTO factory_info
    (id, image, factory_name, introduction, service_hotline, official_wechat, address, copyright_info, create_time, update_time)
VALUES
    (1, 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/factory-demo.jpg', 'NoobShop演示工厂', '用于本地开发和演示的工厂信息，展示平台品牌、客服和售后说明。', '400-800-9000', 'NoobShopService', '浙江省杭州市余杭区未来科技城演示路88号', 'Copyright 2026 NoobShop Demo', NOW(), NOW())
ON DUPLICATE KEY UPDATE image = VALUES(image), factory_name = VALUES(factory_name), introduction = VALUES(introduction), service_hotline = VALUES(service_hotline), official_wechat = VALUES(official_wechat), address = VALUES(address), copyright_info = VALUES(copyright_info), update_time = NOW();

INSERT INTO faq
    (id, question, answer, category, sort, status, create_time, update_time)
VALUES
    (82001, '下单后多久发货？', '现货商品通常在付款后24小时内发出，节假日顺延。', '订单配送', 1, 1, NOW(), NOW()),
    (82002, '可以修改收货地址吗？', '订单发货前可联系客服修改收货地址，已发货订单请联系快递处理。', '订单配送', 2, 1, NOW(), NOW()),
    (82003, '企业价如何使用？', '部分商品设置了企业批量价格，达到采购条件后可在商品详情或结算页查看。', '企业采购', 3, 1, NOW(), NOW()),
    (82004, '商品支持退换货吗？', '不影响二次销售的商品可按平台售后规则申请退换货。', '售后服务', 4, 1, NOW(), NOW()),
    (82005, '优惠活动在哪里看？', '首页轮播、公告和商品详情页会展示当前可用活动。', '活动优惠', 5, 1, NOW(), NOW()),
    (82006, '如何查看物流？', '订单发货后可在订单详情页查看物流公司和物流单号。', '订单配送', 6, 1, NOW(), NOW()),
    (82007, '评价后还能追评吗？', '订单完成并发布首评后，可在评论入口补充追评内容。', '评价相关', 7, 1, NOW(), NOW()),
    (82008, '忘记密码怎么办？', '本地演示环境请使用测试账号，正式环境请走手机号或管理员重置流程。', '账号相关', 8, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question), answer = VALUES(answer), category = VALUES(category), sort = VALUES(sort), status = VALUES(status), update_time = NOW();

INSERT INTO notice
    (id, title, content, status, create_time, update_time)
VALUES
    (83001, '端午假期发货安排', '<p>端午假期期间仓库轮班发货，部分偏远地区时效可能延长。</p>', 1, NOW(), NOW()),
    (83002, '企业采购演示数据说明', '<p>当前数据库包含企业价格、规格、订单和评价等本地演示数据。</p>', 1, NOW(), NOW()),
    (83003, '售后服务时间调整', '<p>在线客服服务时间为每日09:00-22:00。</p>', 1, NOW(), NOW()),
    (83004, '积分功能灰度提示', '<p>积分功能仍在开发中，本地演示环境暂不产生真实积分。</p>', 0, NOW(), NOW()),
    (83005, '新品类目已上架', '<p>数码充电、宠物用品、办公文具等类目已补充商品数据。</p>', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), status = VALUES(status), update_time = NOW();

INSERT INTO address
    (id, user_id, receiver, phone, province, city, district, detail_address, is_default, create_time, update_time)
VALUES
    (84001, 90001, '陈小满', '13800009001', '浙江省', '杭州市', '西湖区', '文三路188号未来公寓3幢1201室', 1, NOW(), NOW()),
    (84002, 90001, '陈小满', '13800009001', '浙江省', '杭州市', '滨江区', '江南大道588号创意园A座801室', 0, NOW(), NOW()),
    (84003, 90002, '林一诺', '13800009002', '江苏省', '南京市', '建邺区', '云锦路66号江湾花园7幢502室', 1, NOW(), NOW()),
    (84004, 90003, '王嘉禾', '13800009003', '广东省', '深圳市', '南山区', '科技园南区科苑路15号6楼', 1, NOW(), NOW()),
    (84005, 90004, '赵安然', '13800009004', '四川省', '成都市', '武侯区', '天府大道中段999号1单元1808室', 1, NOW(), NOW()),
    (84006, 90005, '孙若溪', '13800009005', '湖北省', '武汉市', '洪山区', '珞喻路129号光谷中心B座903室', 1, NOW(), NOW()),
    (84007, 90002, '林一诺', '13800009002', '上海市', '上海市', '浦东新区', '张江高科路88号2号楼1106室', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), receiver = VALUES(receiver), phone = VALUES(phone), province = VALUES(province), city = VALUES(city), district = VALUES(district), detail_address = VALUES(detail_address), is_default = VALUES(is_default), update_time = NOW();

INSERT INTO product_spec
    (id, product_id, spec_text, price, enterprise_price, stock, create_time, update_time)
VALUES
    (85001, 10001, '标准款', 199.90, 179.91, 46, NOW(), NOW()),
    (85002, 10001, '家庭套装', 219.90, 197.91, 46, NOW(), NOW()),
    (85003, 10002, '透明款', 69.90, 62.91, 641, NOW(), NOW()),
    (85004, 10002, '磨砂款', 79.90, 71.91, 641, NOW(), NOW()),
    (85005, 10003, '单件装', 79.90, 71.91, 312, NOW(), NOW()),
    (85006, 10003, '三件装', 109.90, 98.91, 312, NOW(), NOW()),
    (85007, 10004, '基础款', 59.90, 53.91, 548, NOW(), NOW()),
    (85008, 10005, '升级款', 114.90, 103.41, 217, NOW(), NOW()),
    (85009, 10007, '白色款', 49.90, 44.91, 638, NOW(), NOW()),
    (85010, 10009, '尝鲜装', 199.90, 179.91, 313, NOW(), NOW()),
    (85011, 10010, '大包装', 59.90, 53.91, 498, NOW(), NOW()),
    (85012, 10020, '便携装', 49.90, 44.91, 91, NOW(), NOW()),
    (85013, 10025, '经典款', 99.90, 89.91, 188, NOW(), NOW()),
    (85014, 10031, '曜石黑', 179.90, 161.91, 215, NOW(), NOW()),
    (85015, 10044, '双件套', 91.90, 82.71, 249, NOW(), NOW()),
    (85016, 10055, '收藏款', 159.90, 143.91, 151, NOW(), NOW())
ON DUPLICATE KEY UPDATE product_id = VALUES(product_id), spec_text = VALUES(spec_text), price = VALUES(price), enterprise_price = VALUES(enterprise_price), stock = VALUES(stock), update_time = NOW();

INSERT INTO product_search_keyword
    (id, keyword, is_hot, is_show, create_time, update_time)
VALUES
    (86001, '手机壳', 1, 1, NOW(), NOW()),
    (86002, '充电头', 1, 1, NOW(), NOW()),
    (86003, '无线耳机', 1, 1, NOW(), NOW()),
    (86004, '移动电源', 1, 1, NOW(), NOW()),
    (86005, '收纳盒', 1, 1, NOW(), NOW()),
    (86006, '杯垫', 1, 1, NOW(), NOW()),
    (86007, '护手霜', 1, 1, NOW(), NOW()),
    (86008, '宠物零食', 1, 1, NOW(), NOW()),
    (86009, '积木套装', 0, 1, NOW(), NOW()),
    (86010, '遮阳帽', 0, 1, NOW(), NOW()),
    (86011, '厨房用品', 0, 1, NOW(), NOW()),
    (86012, '办公文具', 0, 1, NOW(), NOW()),
    (86013, '硅胶', 0, 1, NOW(), NOW()),
    (86014, '铝合金', 0, 1, NOW(), NOW()),
    (86015, '亚克力', 0, 1, NOW(), NOW()),
    (86016, '不锈钢', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE keyword = VALUES(keyword), is_hot = VALUES(is_hot), is_show = VALUES(is_show), update_time = NOW();

INSERT INTO cart
    (id, user_id, product_id, spec_id, quantity, checked, create_time, update_time)
VALUES
    (87001, 90001, 10001, 85001, 2, 1, NOW(), NOW()),
    (87002, 90001, 10007, 85009, 1, 1, NOW(), NOW()),
    (87003, 90002, 10003, 85005, 3, 0, NOW(), NOW()),
    (87004, 90002, 10025, 85013, 1, 1, NOW(), NOW()),
    (87005, 90003, 10009, 85010, 2, 1, NOW(), NOW()),
    (87006, 90003, 10031, 85014, 1, 0, NOW(), NOW()),
    (87007, 90004, 10044, 85015, 2, 1, NOW(), NOW()),
    (87008, 90004, 10055, 85016, 1, 1, NOW(), NOW()),
    (87009, 90005, 10020, 85012, 4, 0, NOW(), NOW()),
    (87010, 90005, 10002, 85003, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), checked = VALUES(checked), update_time = NOW();

INSERT INTO collection
    (id, user_id, product_id, create_time)
VALUES
    (88001, 90001, 10001, NOW()),
    (88002, 90001, 10020, NOW()),
    (88003, 90001, 10055, NOW()),
    (88004, 90002, 10003, NOW()),
    (88005, 90002, 10025, NOW()),
    (88006, 90003, 10009, NOW()),
    (88007, 90003, 10031, NOW()),
    (88008, 90004, 10044, NOW()),
    (88009, 90004, 10055, NOW()),
    (88010, 90005, 10002, NOW()),
    (88011, 90005, 10007, NOW()),
    (88012, 90005, 10010, NOW())
ON DUPLICATE KEY UPDATE create_time = VALUES(create_time);

INSERT INTO feedback
    (id, user_id, content, image_urls, contact, reply_content, reply_admin_id, status, create_time, reply_time)
VALUES
    (89001, 90001, '商品包装完整，建议订单详情页增加预计送达时间。', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/A1KqYQhL6OL.jpg', '13800009001', '已记录，会在后续版本优化预计送达展示。', 80001, 1, '2026-06-01 10:20:00', '2026-06-01 11:00:00'),
    (89002, 90002, '希望收藏列表可以按照类目筛选。', NULL, 'buyer_lin@example.com', '感谢建议，已提交产品评审。', 80001, 1, '2026-06-01 12:40:00', '2026-06-01 14:10:00'),
    (89003, 90003, '宠物用品分类图标和商品风格可以更统一。', NULL, '13800009003', NULL, NULL, 0, '2026-06-02 09:12:00', NULL),
    (89004, 90004, '售后入口比较好找，物流信息希望更详细。', NULL, '13800009004', '我们会补充更多物流节点展示。', 80001, 2, '2026-06-02 15:32:00', '2026-06-02 16:20:00'),
    (89005, 90005, '搜索关键词联想速度很快，建议增加热搜榜。', NULL, '13800009005', NULL, NULL, 0, '2026-06-03 09:18:00', NULL)
ON DUPLICATE KEY UPDATE content = VALUES(content), image_urls = VALUES(image_urls), contact = VALUES(contact), reply_content = VALUES(reply_content), reply_admin_id = VALUES(reply_admin_id), status = VALUES(status), reply_time = VALUES(reply_time);

INSERT INTO `order`
    (id, order_no, user_id, address_id, total_goods_amount, freight, total_amount, status, pay_type, pay_time, deliver_time, receive_time, cancel_time, cancel_reason, remark, is_evaluate, is_deleted, logistics_company, logistics_no, create_time, update_time)
VALUES
    (900001, 'NS202605280001', 90001, 84001, 449.70, 0.00, 449.70, 5, 2, '2026-05-28 09:20:00', '2026-05-28 16:35:00', '2026-05-31 10:12:00', NULL, NULL, '工作日白天配送', 1, 0, '顺丰速运', 'SF202605280001', '2026-05-28 09:18:00', NOW()),
    (900002, 'NS202605290002', 90002, 84003, 259.70, 6.00, 265.70, 4, 1, '2026-05-29 14:06:00', '2026-05-30 09:15:00', NULL, NULL, NULL, '请放门卫', 0, 0, '中通快递', 'ZTO202605290002', '2026-05-29 14:02:00', NOW()),
    (900003, 'NS202605300003', 90003, 84004, 379.80, 0.00, 379.80, 3, 2, '2026-05-30 20:21:00', NULL, NULL, NULL, NULL, '发货前请检查包装', 0, 0, NULL, NULL, '2026-05-30 20:18:00', NOW()),
    (900004, 'NS202605310004', 90004, 84005, 183.80, 8.00, 191.80, 2, 1, '2026-05-31 11:40:00', NULL, NULL, NULL, NULL, '企业采购样品', 0, 0, NULL, NULL, '2026-05-31 11:35:00', NOW()),
    (900005, 'NS202606010005', 90005, 84006, 149.70, 0.00, 149.70, 1, 0, NULL, NULL, NULL, NULL, NULL, '待确认优惠后支付', 0, 0, NULL, NULL, '2026-06-01 19:26:00', NOW()),
    (900006, 'NS202606020006', 90001, 84002, 159.90, 0.00, 159.90, 6, 0, NULL, NULL, NULL, '2026-06-02 10:10:00', '临时不需要了', NULL, 0, 0, NULL, NULL, '2026-06-02 09:50:00', NOW())
ON DUPLICATE KEY UPDATE total_goods_amount = VALUES(total_goods_amount), freight = VALUES(freight), total_amount = VALUES(total_amount), status = VALUES(status), pay_type = VALUES(pay_type), pay_time = VALUES(pay_time), deliver_time = VALUES(deliver_time), receive_time = VALUES(receive_time), cancel_time = VALUES(cancel_time), cancel_reason = VALUES(cancel_reason), remark = VALUES(remark), is_evaluate = VALUES(is_evaluate), is_deleted = VALUES(is_deleted), logistics_company = VALUES(logistics_company), logistics_no = VALUES(logistics_no), update_time = NOW();

INSERT INTO order_item
    (id, order_id, product_id, spec_id, product_name, spec_text, product_image, price, quantity, subtotal, create_time)
VALUES
    (910001, 900001, 10001, 85001, '品牌TPU密封罐', '标准款', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/A1KqYQhL6OL.jpg', 199.90, 2, 399.80, NOW()),
    (910002, 900001, 10007, 85009, '热销TPU充电头', '白色款', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/912IBVDWtnL.jpg', 49.90, 1, 49.90, NOW()),
    (910003, 900002, 10003, 85005, '热销亚克力杯垫', '单件装', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/91Nar%2B4rtIL.jpg', 79.90, 2, 159.80, NOW()),
    (910004, 900002, 10025, 85013, '精选尼龙无线耳机', '经典款', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/81AIVuIneML.jpg', 99.90, 1, 99.90, NOW()),
    (910005, 900003, 10009, 85010, '精选TPU宠物零食', '尝鲜装', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/910iQLeJ%2BJL.jpg', 199.90, 1, 199.90, NOW()),
    (910006, 900003, 10031, 85014, '网红硅胶无线耳机', '曜石黑', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/81DI9hcVyQL.jpg', 179.90, 1, 179.90, NOW()),
    (910007, 900004, 10044, 85015, '爆款TPU保鲜盒', '双件套', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/81kSrg9MIIL.jpg', 91.90, 2, 183.80, NOW()),
    (910008, 900005, 10020, 85012, '网红TPU护手霜', '便携装', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/916lYWLdbYL.jpg', 49.90, 3, 149.70, NOW()),
    (910009, 900006, 10055, 85016, '原装亚克力手办模型', '收藏款', 'https://noobshop.oss-cn-beijing.aliyuncs.com/image/81sYcgbeQdL.jpg', 159.90, 1, 159.90, NOW())
ON DUPLICATE KEY UPDATE product_id = VALUES(product_id), spec_id = VALUES(spec_id), product_name = VALUES(product_name), spec_text = VALUES(spec_text), product_image = VALUES(product_image), price = VALUES(price), quantity = VALUES(quantity), subtotal = VALUES(subtotal);

INSERT INTO order_tracking
    (id, logistics_no, order_id, logistics_status, location, description, create_time)
VALUES
    (920001, 'SF202605280001', 900001, 4, '浙江省杭州市西湖区', '客户已签收，感谢使用顺丰速运。', '2026-05-31 10:12:00'),
    (920002, 'ZTO202605290002', 900002, 3, '江苏省南京市建邺区', '快件正在派送中，请保持电话畅通。', '2026-06-01 09:30:00'),
    (920003, 'YS202606010003', 900003, 1, '广东省深圳市南山区', '商家已通知仓库揽收。', '2026-06-01 18:20:00')
ON DUPLICATE KEY UPDATE order_id = VALUES(order_id), logistics_status = VALUES(logistics_status), location = VALUES(location), description = VALUES(description), create_time = VALUES(create_time);

INSERT INTO product_comment
    (id, product_id, product_spec_id, product_spec_text, order_id, user_id, user_nickname, user_avatar, parent_id, reply_user_id, reply_user_nickname, is_buyer, is_anonymous, is_append_comment, is_good_review, rating, content, image_urls, like_count, status, create_time, update_time)
VALUES
    (930001, 10001, 85001, '标准款', 900001, 90001, '陈小满', '/static/images/default-avatar.png', 0, NULL, NULL, 1, 1, 0, 1, 5, '密封罐质感比预期好，盖子很紧，厨房收纳清爽很多。', '["https://noobshop.oss-cn-beijing.aliyuncs.com/image/A1KqYQhL6OL.jpg"]', 12, 1, '2026-05-31 12:10:00', NOW()),
    (930002, 10007, 85009, '白色款', 900001, 90001, '陈小满', '/static/images/default-avatar.png', 0, NULL, NULL, 1, 1, 0, 1, 5, '充电头体积小，出门带一个就够。', NULL, 8, 1, '2026-05-31 12:16:00', NOW()),
    (930003, 10003, 85005, '单件装', 900002, 90002, '林一诺', '/static/images/default-avatar.png', 0, NULL, NULL, 1, 1, 0, 0, 4, '杯垫颜色好看，包装再厚一点会更好。', '["https://noobshop.oss-cn-beijing.aliyuncs.com/image/91Nar%2B4rtIL.jpg"]', 5, 1, '2026-06-01 19:08:00', NOW()),
    (930004, 10009, 85010, '尝鲜装', 900003, 90003, '王嘉禾', '/static/images/default-avatar.png', 0, NULL, NULL, 1, 0, 0, 1, 5, '宠物零食适口性不错，分装也方便。', NULL, 9, 1, '2026-06-02 10:35:00', NOW()),
    (930005, 10044, 85015, '双件套', 900004, 90004, '赵安然', '/static/images/default-avatar.png', 0, NULL, NULL, 1, 0, 0, 0, 4, '保鲜盒容量合适，企业样品采购先试一批。', NULL, 3, 1, '2026-06-02 14:25:00', NOW()),
    (930006, 10020, 85012, '便携装', 900005, 90005, '孙若溪', '/static/images/default-avatar.png', 0, NULL, NULL, 0, 1, 0, 1, 5, '护手霜小支便携，香味比较清淡。', NULL, 4, 1, '2026-06-03 08:45:00', NOW())
ON DUPLICATE KEY UPDATE product_spec_id = VALUES(product_spec_id), product_spec_text = VALUES(product_spec_text), order_id = VALUES(order_id), user_id = VALUES(user_id), user_nickname = VALUES(user_nickname), user_avatar = VALUES(user_avatar), is_buyer = VALUES(is_buyer), is_anonymous = VALUES(is_anonymous), is_good_review = VALUES(is_good_review), rating = VALUES(rating), content = VALUES(content), image_urls = VALUES(image_urls), like_count = VALUES(like_count), status = VALUES(status), update_time = NOW();

INSERT INTO product_comment_append
    (id, comment_id, product_id, product_spec_id, order_id, user_id, content, image_urls, status, create_time)
VALUES
    (940001, 930001, 10001, 85001, 900001, 90001, '用了几天没有异味，清洗也方便。', NULL, 1, '2026-06-02 09:10:00'),
    (940002, 930003, 10003, 85005, 900002, 90002, '客服补发了外包装，处理速度可以。', NULL, 1, '2026-06-03 10:25:00')
ON DUPLICATE KEY UPDATE product_id = VALUES(product_id), product_spec_id = VALUES(product_spec_id), order_id = VALUES(order_id), user_id = VALUES(user_id), content = VALUES(content), image_urls = VALUES(image_urls), status = VALUES(status), create_time = VALUES(create_time);

INSERT INTO chat_session
    (id, user_id, contact_id, last_msg_content, last_msg_time, unread_count_a, unread_count_b, update_time)
VALUES
    (950001, 90000, 90001, '密封罐还有家庭套装吗？', '2026-06-03 09:20:00', 0, 1, NOW()),
    (950002, 90000, 90002, '请问南京预计几天到？', '2026-06-02 18:40:00', 0, 0, NOW()),
    (950003, 90000, 90003, '宠物零食适合小型犬吗？', '2026-06-02 10:05:00', 1, 0, NOW()),
    (950004, 90000, 90004, '企业采购可以开发票吗？', '2026-06-01 16:30:00', 0, 2, NOW()),
    (950005, 90000, 90005, '护手霜还有组合装吗？', '2026-06-03 08:55:00', 0, 0, NOW())
ON DUPLICATE KEY UPDATE last_msg_content = VALUES(last_msg_content), last_msg_time = VALUES(last_msg_time), unread_count_a = VALUES(unread_count_a), unread_count_b = VALUES(unread_count_b), update_time = NOW();

INSERT INTO chat_message
    (id, from_user_id, to_user_id, content, msg_type, product_id, is_read, create_time)
VALUES
    (960001, 90001, 90000, '密封罐还有家庭套装吗？', 0, 10001, 1, '2026-06-03 09:18:00'),
    (960002, 90000, 90001, '有的，家庭套装库存充足。', 0, 10001, 0, '2026-06-03 09:20:00'),
    (960003, 90002, 90000, '请问南京预计几天到？', 0, NULL, 1, '2026-06-02 18:40:00'),
    (960004, 90003, 90000, '宠物零食适合小型犬吗？', 0, 10009, 0, '2026-06-02 10:05:00'),
    (960005, 90004, 90000, '企业采购可以开发票吗？', 0, 10044, 1, '2026-06-01 16:30:00'),
    (960006, 90000, 90004, '可以，请在订单备注中填写开票信息。', 0, 10044, 0, '2026-06-01 16:32:00'),
    (960007, 90005, 90000, '护手霜还有组合装吗？', 0, 10020, 0, '2026-06-03 08:55:00')
ON DUPLICATE KEY UPDATE content = VALUES(content), msg_type = VALUES(msg_type), product_id = VALUES(product_id), is_read = VALUES(is_read), create_time = VALUES(create_time);

INSERT INTO operation_log
    (id, admin_id, operation, operation_url, operation_method, operation_ip, request_params, operation_time)
VALUES
    (970001, 80001, '导入商品种子数据', '/admin/product/import', 'POST', '127.0.0.1', '{"source":"product.sql","rows":500}', '2026-06-01 09:30:00'),
    (970002, 80001, '更新首页轮播', '/admin/banner/save', 'POST', '127.0.0.1', '{"count":6}', '2026-06-01 10:12:00'),
    (970003, 80001, '处理用户反馈', '/admin/feedback/reply', 'PUT', '127.0.0.1', '{"feedbackId":89001}', '2026-06-01 11:00:00'),
    (970004, 80001, '维护公告', '/admin/notice/save', 'POST', '127.0.0.1', '{"noticeId":83003}', '2026-06-02 15:20:00'),
    (970005, 80001, '查看订单列表', '/admin/order/page', 'GET', '127.0.0.1', '{"status":4}', '2026-06-03 09:30:00')
ON DUPLICATE KEY UPDATE operation = VALUES(operation), operation_url = VALUES(operation_url), operation_method = VALUES(operation_method), operation_ip = VALUES(operation_ip), request_params = VALUES(request_params), operation_time = VALUES(operation_time);
