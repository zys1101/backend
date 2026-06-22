-- ==========================================
-- 联系人管理系统 V2.0 数据库变更脚本
-- ==========================================

-- 1. 标签表
CREATE TABLE IF NOT EXISTS `contact_tag` (
    `tag_id` VARCHAR(20) NOT NULL COMMENT '标签ID',
    `tag_name` VARCHAR(30) NOT NULL COMMENT '标签名称',
    `user_id` VARCHAR(20) NOT NULL COMMENT '所属用户ID',
    `tag_color` VARCHAR(20) DEFAULT '#1890ff' COMMENT '标签颜色',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`tag_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人标签表';

-- 2. 联系人-标签关联表
CREATE TABLE IF NOT EXISTS `contact_tag_rel` (
    `ct_id` VARCHAR(20) NOT NULL COMMENT '联系人ID',
    `tag_id` VARCHAR(20) NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`ct_id`, `tag_id`),
    INDEX `idx_ct_id` (`ct_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人-标签关联表';

-- 3. 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `log_id` VARCHAR(20) NOT NULL COMMENT '日志ID',
    `user_id` VARCHAR(20) NOT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `operation_desc` VARCHAR(200) NOT NULL COMMENT '操作描述',
    `request_url` VARCHAR(200) DEFAULT NULL COMMENT '请求URL',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `operation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`log_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 4. 用户主题偏好表（深色模式）
CREATE TABLE IF NOT EXISTS `user_theme` (
    `user_id` VARCHAR(20) NOT NULL COMMENT '用户ID',
    `theme` VARCHAR(20) DEFAULT 'light' COMMENT '主题：light/dark',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主题偏好表';

-- 5. 初始化系统默认标签（可选）
-- INSERT INTO `contact_tag` (`tag_id`, `tag_name`, `user_id`, `tag_color`, `created_at`) VALUES
-- ('T000000001', '家人', 'U000000001', '#faad14', NOW()),
-- ('T000000002', '同学', 'U000000001', '#1890ff', NOW()),
-- ('T000000003', '客户', 'U000000001', '#52c41a', NOW()),
-- ('T000000004', '老师', 'U000000001', '#722ed1', NOW()),
-- ('T000000005', '同事', 'U000000001', '#13c2c2', NOW()),
-- ('T000000006', '朋友', 'U000000001', '#eb2f96', NOW());
