-- Fine-grained API permissions used by Spring Security method authorization.
INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'product:manage', '商品管理', 2, 0, 10, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'product:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'category:manage', '分类管理', 2, 0, 20, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'category:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'banner:manage', '轮播图管理', 2, 0, 30, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'banner:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'notice:manage', '通知管理', 2, 0, 40, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'notice:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'coupon:release', '发布优惠券', 2, 0, 50, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'coupon:release');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'rbac:manage', '角色权限管理', 2, 0, 60, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'rbac:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'file:upload', '文件上传', 2, 0, 70, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'file:upload');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'user:read', '查看用户信息', 2, 0, 80, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'user:read');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'user:update', '修改用户信息', 2, 0, 90, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'user:update');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'cart:read', '查看购物车', 2, 0, 100, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'cart:read');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'cart:write', '修改购物车', 2, 0, 110, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'cart:write');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'address:manage', '地址管理', 2, 0, 120, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'address:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'collection:manage', '收藏管理', 2, 0, 130, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'collection:manage');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'order:read', '查看订单', 2, 0, 140, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'order:read');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'order:create', '创建订单', 2, 0, 150, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'order:create');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'order:cancel', '取消订单', 2, 0, 160, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'order:cancel');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'order:pay', '订单支付', 2, 0, 170, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'order:pay');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'order:receive', '确认收货', 2, 0, 180, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'order:receive');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'comment:write', '发表评论', 2, 0, 190, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:write');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'comment:like', '评论点赞', 2, 0, 200, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'comment:like');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'chat:access', '在线客服', 2, 0, 210, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'chat:access');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, sort, is_enable, create_time)
SELECT 'feedback:create', '提交反馈', 2, 0, 220, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'feedback:create');

-- The administrator role receives all management permissions by default.
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'product:manage',
    'category:manage',
    'banner:manage',
    'notice:manage',
    'coupon:release',
    'rbac:manage',
    'file:upload',
    'user:read',
    'user:update',
    'cart:read',
    'cart:write',
    'address:manage',
    'collection:manage',
    'order:read',
    'order:create',
    'order:cancel',
    'order:pay',
    'order:receive',
    'comment:write',
    'comment:like',
    'chat:access',
    'feedback:create'
)
WHERE r.role_code = 'ROLE_ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission relation
      WHERE relation.role_id = r.id
        AND relation.perm_id = p.id
  );

-- Ordinary buyers receive only end-user capabilities.
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'user:read', 'user:update',
    'cart:read', 'cart:write',
    'address:manage', 'collection:manage',
    'order:read', 'order:create', 'order:cancel', 'order:pay', 'order:receive',
    'comment:write', 'comment:like', 'chat:access', 'feedback:create'
)
WHERE r.role_code = 'ROLE_BUYER'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission relation
      WHERE relation.role_id = r.id
        AND relation.perm_id = p.id
  );
