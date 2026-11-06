-- =========================================================
-- 增量迁移：预约加油功能（适用于已有数据库，如 alvin_gas）
-- 用法：mysql -uroot -p <数据库名> < sql/migration_reserve.sql
-- =========================================================
SET NAMES utf8mb4;

-- 会员表：当前车牌号 + 默认加油站
ALTER TABLE `gs_member`
    ADD COLUMN `plate_no` VARCHAR(16) DEFAULT NULL COMMENT '当前车牌号' AFTER `phone`,
    ADD COLUMN `default_station_id` BIGINT DEFAULT NULL COMMENT '默认加油站 ID' AFTER `plate_no`;

-- 预约加油订单
CREATE TABLE IF NOT EXISTS `gs_reserve_order` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `reserve_no`      VARCHAR(32)    NOT NULL COMMENT '预约单号',
    `member_id`       BIGINT         NOT NULL COMMENT '会员 ID',
    `station_id`      BIGINT         NOT NULL COMMENT '油站 ID',
    `station_name`    VARCHAR(128)   DEFAULT NULL COMMENT '油站名称（快照）',
    `plate_no`        VARCHAR(16)    NOT NULL COMMENT '车牌号',
    `fuel_type`       VARCHAR(16)    NOT NULL COMMENT '油品 92#/95#/98#/0#',
    `unit_price`      DECIMAL(10, 2) DEFAULT NULL COMMENT '预约时单价（元/升）',
    `liters`          DECIMAL(10, 2) NOT NULL COMMENT '预加油量（升）',
    `est_amount`      DECIMAL(10, 2) DEFAULT NULL COMMENT '预估金额（元）',
    `expect_time`     DATETIME       NOT NULL COMMENT '预计到达时间',
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '0 待确认 1 已加油 2 已拒绝 3 已取消',
    `reply`           VARCHAR(255)   DEFAULT NULL COMMENT '后台回复（拒绝原因等）',
    `handle_time`     DATETIME       DEFAULT NULL COMMENT '后台处理时间',
    `remark`          VARCHAR(255)   DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reserve_no` (`reserve_no`),
    KEY `idx_member` (`member_id`, `status`),
    KEY `idx_status_expect` (`status`, `expect_time`),
    KEY `idx_plate` (`plate_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '预约加油订单';
