-- =========================================================
-- 智慧加油站平台 - 数据库脚本（MySQL 8）
-- 默认密码：123456
-- =========================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `gs_user`;
CREATE TABLE `gs_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
    `real_name`   VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        VARCHAR(20)  NOT NULL COMMENT 'ADMIN/MANAGER/STAFF',
    `station_id`  BIGINT       DEFAULT NULL COMMENT '所属油站',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '后台账号';

DROP TABLE IF EXISTS `gs_station`;
CREATE TABLE `gs_station` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`           VARCHAR(128)  NOT NULL COMMENT '油站名称',
    `code`           VARCHAR(64)   DEFAULT NULL COMMENT '油站编码（二维码用）',
    `province`       VARCHAR(64)    DEFAULT NULL COMMENT '省',
    `city`           VARCHAR(64)   DEFAULT NULL COMMENT '市',
    `address`        VARCHAR(255)  DEFAULT NULL COMMENT '详细地址',
    `lng`            DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
    `lat`            DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
    `phone`          VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
    `business_hours` VARCHAR(64)   DEFAULT '24小时' COMMENT '营业时间',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '1 营业 0 停业',
    `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '加油站';

DROP TABLE IF EXISTS `gs_gun`;
CREATE TABLE `gs_gun` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `station_id` BIGINT      NOT NULL COMMENT '油站 ID',
    `gun_no`     VARCHAR(16) NOT NULL COMMENT '枪号',
    `fuel_type`  VARCHAR(16) NOT NULL COMMENT '油品 92#/95#/98#/0#',
    `status`     TINYINT     NOT NULL DEFAULT 1 COMMENT '1 空闲 2 使用中 3 故障',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_station` (`station_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '加油枪';

DROP TABLE IF EXISTS `gs_fuel_price`;
CREATE TABLE `gs_fuel_price` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `station_id`     BIGINT         NOT NULL COMMENT '油站 ID',
    `fuel_type`      VARCHAR(16)    NOT NULL COMMENT '油品',
    `list_price`     DECIMAL(10, 2) NOT NULL COMMENT '挂牌价（元/升）',
    `sale_price`     DECIMAL(10, 2) NOT NULL COMMENT '实际售价（元/升）',
    `effective_time` DATETIME       DEFAULT NULL COMMENT '生效时间',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '1 生效 0 失效',
    `create_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_station_fuel` (`station_id`, `fuel_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '油价';

DROP TABLE IF EXISTS `gs_member`;
CREATE TABLE `gs_member` (
    `id`         BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `openid`     VARCHAR(64)    NOT NULL COMMENT '微信 openid',
    `nickname`   VARCHAR(64)    DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(255)   DEFAULT NULL COMMENT '头像',
    `phone`      VARCHAR(20)    DEFAULT NULL COMMENT '手机号',
    `balance`    DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    `points`     INT            NOT NULL DEFAULT 0 COMMENT '积分',
    `level`      TINYINT        NOT NULL DEFAULT 1 COMMENT '1 普通 2 银卡 3 金卡',
    `status`     TINYINT        NOT NULL DEFAULT 1 COMMENT '1 正常 0 禁用',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会员';

DROP TABLE IF EXISTS `gs_coupon`;
CREATE TABLE `gs_coupon` (
    `id`         BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       VARCHAR(128)   NOT NULL COMMENT '券名称',
    `type`       TINYINT        NOT NULL DEFAULT 1 COMMENT '1 满减 2 折扣',
    `threshold`  DECIMAL(10, 2) DEFAULT NULL COMMENT '使用门槛',
    `amount`     DECIMAL(10, 2)  DEFAULT NULL COMMENT '优惠金额（满减）',
    `discount`   DECIMAL(4, 2)   DEFAULT NULL COMMENT '折扣率，如 0.95（折扣）',
    `total`      INT            NOT NULL DEFAULT 0 COMMENT '发放总量',
    `remain`     INT            NOT NULL DEFAULT 0 COMMENT '剩余数量',
    `start_time` DATETIME       DEFAULT NULL COMMENT '生效时间',
    `end_time`   DATETIME       DEFAULT NULL COMMENT '过期时间',
    `status`     TINYINT        NOT NULL DEFAULT 1 COMMENT '1 上架 0 下架',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '优惠券模板';

DROP TABLE IF EXISTS `gs_member_coupon`;
CREATE TABLE `gs_member_coupon` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `member_id`       BIGINT         NOT NULL COMMENT '会员 ID',
    `coupon_id`       BIGINT         NOT NULL COMMENT '券模板 ID',
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '0 未使用 1 已使用 2 已过期',
    `discount_amount` DECIMAL(10, 2) DEFAULT NULL COMMENT '实际抵扣金额',
    `order_id`        BIGINT         DEFAULT NULL COMMENT '使用订单',
    `obtain_time`     DATETIME       DEFAULT NULL COMMENT '领取时间',
    `use_time`        DATETIME       DEFAULT NULL COMMENT '使用时间',
    `create_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_member` (`member_id`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会员优惠券';

DROP TABLE IF EXISTS `gs_order`;
CREATE TABLE `gs_order` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`        VARCHAR(32)    NOT NULL COMMENT '订单号',
    `member_id`       BIGINT         NOT NULL COMMENT '会员 ID',
    `station_id`      BIGINT         NOT NULL COMMENT '油站 ID',
    `gun_id`          BIGINT         DEFAULT NULL COMMENT '加油枪 ID',
    `gun_no`          VARCHAR(16)    DEFAULT NULL COMMENT '枪号',
    `fuel_type`       VARCHAR(16)    NOT NULL COMMENT '油品',
    `unit_price`      DECIMAL(10, 2) NOT NULL COMMENT '单价',
    `liters`          DECIMAL(10, 2) NOT NULL COMMENT '升数',
    `amount`          DECIMAL(10, 2) NOT NULL COMMENT '原始金额',
    `coupon_id`       BIGINT         DEFAULT NULL COMMENT '使用的会员券 ID',
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount`      DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    `points`          INT            DEFAULT 0 COMMENT '获得积分',
    `pay_type`        VARCHAR(20)    DEFAULT NULL COMMENT 'balance/wx',
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '0 待支付 1 已支付 2 已完成 3 已取消 4 已退款',
    `pay_time`        DATETIME       DEFAULT NULL COMMENT '支付时间',
    `remark`          VARCHAR(255)   DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_member` (`member_id`),
    KEY `idx_station` (`station_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '加油订单';

DROP TABLE IF EXISTS `gs_balance_log`;
CREATE TABLE `gs_balance_log` (
    `id`            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `member_id`     BIGINT         NOT NULL COMMENT '会员 ID',
    `type`          TINYINT        NOT NULL COMMENT '1 充值 2 消费 3 退款 4 调整',
    `amount`        DECIMAL(10, 2) NOT NULL COMMENT '变动金额（正增负减）',
    `balance_after` DECIMAL(10, 2) DEFAULT NULL COMMENT '变动后余额',
    `biz_no`        VARCHAR(64)    DEFAULT NULL COMMENT '业务单号',
    `remark`        VARCHAR(255)   DEFAULT NULL COMMENT '备注',
    `create_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_member` (`member_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '余额积分流水';

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- 初始化数据
-- =========================================================
-- 后台账号：admin / manager / staff，密码均为 123456
INSERT INTO `gs_user` (`username`, `password`, `real_name`, `phone`, `role`, `station_id`, `status`)
VALUES ('admin', '$2a$10$Hx.yY8Ohas6BSeH8qvk7a.TEMCk/86gp8VFbD0N1sFFYnXVF71hRC', '超级管理员', '13800000000', 'ADMIN', NULL, 1),
       ('manager', '$2a$10$Hx.yY8Ohas6BSeH8qvk7a.TEMCk/86gp8VFbD0N1sFFYnXVF71hRC', '张站长', '13800000001', 'MANAGER', 1, 1),
       ('staff', '$2a$10$Hx.yY8Ohas6BSeH8qvk7a.TEMCk/86gp8VFbD0N1sFFYnXVF71hRC', '李加油员', '13800000002', 'STAFF', 1, 1);

-- 油站
INSERT INTO `gs_station` (`name`, `code`, `province`, `city`, `address`, `lng`, `lat`, `phone`, `business_hours`, `status`)
VALUES ('智慧加油站（国贸店）', 'S1001', '北京市', '北京市', '朝阳区建国门外大街1号', 116.461, 39.909, '010-88880001', '24小时', 1),
       ('智慧加油站（望京店）', 'S1002', '北京市', '北京市', '朝阳区望京街9号', 116.470, 40.000, '010-88880002', '06:00-23:00', 1),
       ('智慧加油站（西二旗店）', 'S1003', '北京市', '北京市', '海淀区西二旗大街26号', 116.310, 40.053, '010-88880003', '24小时', 1);

-- 加油枪
INSERT INTO `gs_gun` (`station_id`, `gun_no`, `fuel_type`, `status`)
VALUES (1, '01', '92#', 1), (1, '02', '95#', 1), (1, '03', '98#', 1), (1, '04', '0#', 1),
       (2, '01', '92#', 1), (2, '02', '95#', 1), (2, '03', '0#', 1),
       (3, '01', '92#', 1), (3, '02', '95#', 1), (3, '03', '98#', 1), (3, '04', '0#', 3);

-- 油价
INSERT INTO `gs_fuel_price` (`station_id`, `fuel_type`, `list_price`, `sale_price`, `effective_time`, `status`)
VALUES (1, '92#', 7.85, 7.45, NOW(), 1), (1, '95#', 8.35, 7.95, NOW(), 1),
       (1, '98#', 9.35, 8.95, NOW(), 1), (1, '0#', 7.55, 7.20, NOW(), 1),
       (2, '92#', 7.85, 7.50, NOW(), 1), (2, '95#', 8.35, 8.00, NOW(), 1), (2, '0#', 7.55, 7.25, NOW(), 1),
       (3, '92#', 7.85, 7.40, NOW(), 1), (3, '95#', 8.35, 7.90, NOW(), 1),
       (3, '98#', 9.35, 8.90, NOW(), 1), (3, '0#', 7.55, 7.15, NOW(), 1);

-- 会员（openid: mock_member_001，未配置微信时可用该 code 登录）
INSERT INTO `gs_member` (`openid`, `nickname`, `avatar`, `phone`, `balance`, `points`, `level`, `status`)
VALUES ('mock_member_001', '体验用户', '', '13900000001', 500.00, 120, 1, 1),
       ('mock_member_002', '王小美', '', '13900000002', 88.00, 30, 1, 1);

-- 优惠券
INSERT INTO `gs_coupon` (`name`, `type`, `threshold`, `amount`, `discount`, `total`, `remain`, `start_time`, `end_time`, `status`)
VALUES ('新人加油满 200 减 15', 1, 200.00, 15.00, NULL, 1000, 980, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
       ('老客专享满 300 减 25', 1, 300.00, 25.00, NULL, 500, 460, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
       ('会员日 95 折券', 2, NULL, NULL, 0.95, 800, 760, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 1);

-- 会员已领券
INSERT INTO `gs_member_coupon` (`member_id`, `coupon_id`, `status`, `obtain_time`)
VALUES (1, 1, 0, NOW()), (1, 3, 0, NOW());

-- 订单示例（已完成）
INSERT INTO `gs_order` (`order_no`, `member_id`, `station_id`, `gun_id`, `gun_no`, `fuel_type`, `unit_price`,
                        `liters`, `amount`, `coupon_id`, `discount_amount`, `pay_amount`, `points`, `pay_type`,
                        `status`, `pay_time`)
VALUES ('GS202601010930000001', 1, 1, 1, '01', '92#', 7.45, 40.00, 298.00, NULL, 0.00, 298.00, 298, 'balance', 2, NOW()),
       ('GS202601021015000002', 2, 2, 5, '01', '92#', 7.50, 20.00, 150.00, NULL, 0.00, 150.00, 150, 'wx', 2, NOW());
