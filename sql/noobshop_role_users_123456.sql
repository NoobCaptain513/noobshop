-- NoobShop role test users.
-- All accounts use the plaintext password: 123456
-- Password is stored as a JBCrypt hash compatible with the project login code.
--
-- This script creates users in the current sys_user role/permission system.
-- It does not insert data into the legacy admin table.

SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO sys_user
    (username, password, nickname, avatar, phone, openid, user_type, is_enable, create_time, update_time)
VALUES
    ('role_super_admin', '$2a$10$lBenSPyd.L5Nv03snbIj9OU7ojk8aDb2M3M5P/AkJJESmg2Kgk/za', '角色测试-超级管理员', '/static/images/default-avatar.png', '13900001001', NULL, 1, 1, NOW(), NOW()),
    ('role_operator', '$2a$10$lBenSPyd.L5Nv03snbIj9OU7ojk8aDb2M3M5P/AkJJESmg2Kgk/za', '角色测试-平台运营', '/static/images/default-avatar.png', '13900001002', NULL, 2, 1, NOW(), NOW()),
    ('role_customer_service', '$2a$10$lBenSPyd.L5Nv03snbIj9OU7ojk8aDb2M3M5P/AkJJESmg2Kgk/za', '角色测试-客服人员', '/static/images/default-avatar.png', '13900001003', NULL, 2, 1, NOW(), NOW()),
    ('role_buyer', '$2a$10$lBenSPyd.L5Nv03snbIj9OU7ojk8aDb2M3M5P/AkJJESmg2Kgk/za', '角色测试-普通买家', '/static/images/default-avatar.png', '13900001004', NULL, 3, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    avatar = VALUES(avatar),
    phone = VALUES(phone),
    user_type = VALUES(user_type),
    is_enable = VALUES(is_enable),
    update_time = NOW();

-- Remove existing role bindings for these test accounts before assigning
-- exactly one intended role to each user.
DELETE sur
FROM sys_user_role sur
JOIN sys_user su ON su.id = sur.user_id
WHERE su.username IN (
    'role_super_admin',
    'role_operator',
    'role_customer_service',
    'role_buyer'
);

INSERT INTO sys_user_role (user_id, role_id)
SELECT su.id, sr.id
FROM sys_user su
JOIN sys_role sr ON sr.role_code = CASE su.username
    WHEN 'role_super_admin' THEN 'ROLE_SUPER_ADMIN'
    WHEN 'role_operator' THEN 'ROLE_OPERATOR'
    WHEN 'role_customer_service' THEN 'ROLE_CUSTOMER_SERVICE'
    WHEN 'role_buyer' THEN 'ROLE_BUYER'
END
WHERE su.username IN (
    'role_super_admin',
    'role_operator',
    'role_customer_service',
    'role_buyer'
)
  AND sr.is_enable = 1;

COMMIT;

-- Verification query.
SELECT
    su.id,
    su.username,
    su.nickname,
    su.user_type,
    su.is_enable,
    sr.role_code,
    sr.role_name
FROM sys_user su
LEFT JOIN sys_user_role sur ON sur.user_id = su.id
LEFT JOIN sys_role sr ON sr.id = sur.role_id
WHERE su.username IN (
    'role_super_admin',
    'role_operator',
    'role_customer_service',
    'role_buyer'
)
ORDER BY su.user_type, su.username;
