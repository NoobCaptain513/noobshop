-- Missing tables required by the current backend code.
-- Run this against the noobshop MySQL database before starting the application.

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS coupon_mutex_group
(
    id          bigint auto_increment comment 'Primary key'
        primary key,
    group_name  varchar(100)                        not null comment 'Mutex group name',
    group_code  bigint                              not null comment 'Unique mutex group code',
    remark      varchar(255)                        null comment 'Remark',
    create_time datetime default CURRENT_TIMESTAMP  not null comment 'Create time',
    constraint uk_coupon_mutex_group_code
        unique (group_code)
)
    comment 'Coupon mutex group table' collate = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupon
(
    id                 bigint auto_increment comment 'Primary key'
        primary key,
    coupon_no          varchar(64)                          not null comment 'Coupon template code',
    activity_name      varchar(100)                         not null comment 'Activity name',
    coupon_type        tinyint                              not null comment '1 full reduction, 2 discount, 3 no threshold, 4 single product',
    face_value         decimal(10, 2)                       null comment 'Coupon amount',
    discount_rate      decimal(5, 2)                        null comment 'Discount rate',
    max_discount       decimal(10, 2)                       null comment 'Max discount amount',
    min_spend          decimal(10, 2) default 0.00          not null comment 'Minimum spend',
    total_quota        int            default 0             not null comment 'Total quota, 0 means unlimited',
    used_quota         int            default 0             not null comment 'Used quota',
    receive_quota      int            default 0             not null comment 'Received quota',
    valid_mode         tinyint                              not null comment '1 fixed time, 2 days after receive',
    valid_start        datetime                             null comment 'Fixed valid start time',
    valid_end          datetime                             null comment 'Fixed valid end time',
    receive_valid_days int                                  null comment 'Valid days after receive',
    limit_per_person   int            default 1             not null comment 'Receive limit per person',
    user_limit_type    tinyint        default 1             not null comment '1 all, 2 new user, 3 member, 4 specified users',
    use_scope          tinyint        default 1             not null comment '1 all, 2 specified products, 3 specified categories',
    mutex_group_code   bigint                               null comment 'Coupon mutex group code',
    status             tinyint        default 0             not null comment '0 not started, 1 active, 2 ended, 3 disabled',
    is_elimination     tinyint        default 0             not null comment '0 visible, 1 eliminated',
    release_time       datetime                             null comment 'Release time',
    create_time        datetime       default CURRENT_TIMESTAMP not null comment 'Create time',
    update_time        datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    constraint uk_coupon_no
        unique (coupon_no)
)
    comment 'Coupon table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_coupon_create_time
    ON coupon (create_time);

CREATE INDEX idx_coupon_mutex_group_code
    ON coupon (mutex_group_code);

CREATE INDEX idx_coupon_status_elimination
    ON coupon (status, is_elimination);

CREATE TABLE IF NOT EXISTS coupon_scope_detail
(
    id          bigint auto_increment comment 'Primary key'
        primary key,
    coupon_id   bigint                             not null comment 'Coupon id',
    scope_type  tinyint                            not null comment '1 product, 2 category',
    target_id   bigint                             not null comment 'Product id or category id',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Create time'
)
    comment 'Coupon scope detail table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_coupon_scope_coupon_id
    ON coupon_scope_detail (coupon_id);

CREATE INDEX idx_coupon_scope_target
    ON coupon_scope_detail (scope_type, target_id);

CREATE TABLE IF NOT EXISTS coupon_user
(
    id          bigint auto_increment comment 'Primary key'
        primary key,
    user_id     bigint                             not null comment 'User id',
    coupon_id   bigint                             not null comment 'Coupon id',
    valid_start datetime                           null comment 'User coupon valid start time',
    valid_end   datetime                           null comment 'User coupon valid end time',
    use_status  tinyint  default 0                 not null comment '0 unbegin, 1 unused, 2 used, 3 expired, 4 returned',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Receive time',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time'
)
    comment 'User coupon table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_coupon_user_coupon_id
    ON coupon_user (coupon_id);

CREATE INDEX idx_coupon_user_user_id
    ON coupon_user (user_id);

CREATE INDEX idx_coupon_user_status
    ON coupon_user (coupon_id, use_status);

CREATE TABLE IF NOT EXISTS coupon_order_rel
(
    id              bigint auto_increment comment 'Primary key'
        primary key,
    order_id        bigint                             not null comment 'Order id',
    order_item_id   bigint                             null comment 'Order item id',
    user_coupon_id  bigint                             not null comment 'User coupon id',
    activity_id     bigint                             not null comment 'Coupon activity id',
    discount_amount decimal(10, 2)                     not null comment 'Discount amount',
    rel_status      tinyint  default 1                 not null comment '1 used, 2 refunded, 3 disabled',
    use_time        datetime                           null comment 'Use time',
    refund_time     datetime                           null comment 'Refund time',
    create_time     datetime default CURRENT_TIMESTAMP not null comment 'Create time'
)
    comment 'Coupon order relation table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_coupon_order_rel_order_id
    ON coupon_order_rel (order_id);

CREATE INDEX idx_coupon_order_rel_user_coupon_id
    ON coupon_order_rel (user_coupon_id);

CREATE TABLE IF NOT EXISTS mq_consumer_failed_msg
(
    id           bigint auto_increment comment 'Primary key'
        primary key,
    msg_id       varchar(128)                        null comment 'RocketMQ message id',
    biz_id       varchar(128)                        null comment 'Business id',
    topic        varchar(128)                        not null comment 'Topic',
    tag          varchar(128)                        null comment 'Tag',
    body         text                                null comment 'Message body JSON',
    retry_count  int       default 0                 not null comment 'Retry count',
    error_msg    text                                null comment 'Error message',
    status       tinyint   default 0                 not null comment '0 pending, 1 handled, 2 ignored',
    create_time  datetime  default CURRENT_TIMESTAMP not null comment 'Create time',
    update_time  datetime  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time'
)
    comment 'MQ consumer failed message table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_mq_failed_status
    ON mq_consumer_failed_msg (status);

CREATE INDEX idx_mq_failed_topic_tag
    ON mq_consumer_failed_msg (topic, tag);

CREATE TABLE IF NOT EXISTS product_comment_like
(
    id          bigint auto_increment comment 'Primary key'
        primary key,
    comment_id  bigint                             not null comment 'Product comment id',
    user_id     bigint                             not null comment 'User id',
    status      tinyint  default 1                 not null comment '0 canceled, 1 liked',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Create time',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    constraint uk_product_comment_like_user
        unique (comment_id, user_id)
)
    comment 'Product comment like table' collate = utf8mb4_unicode_ci;

CREATE INDEX idx_product_comment_like_comment_id
    ON product_comment_like (comment_id);

CREATE INDEX idx_product_comment_like_user_id
    ON product_comment_like (user_id);
