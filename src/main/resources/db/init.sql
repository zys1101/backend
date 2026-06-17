-- 联系人管理系统数据库DDL初始化脚本
-- 数据库版本: MySQL 8.0+

CREATE DATABASE IF NOT EXISTS contact_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE contact_db;

DROP TABLE IF EXISTS matter;
DROP TABLE IF EXISTS contact_pic;
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS user_info;

CREATE TABLE user_info (
    user_id CHAR(10) PRIMARY KEY COMMENT '用户ID',
    user_password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    username VARCHAR(20) COMMENT '用户名',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE contact (
    ct_id CHAR(10) PRIMARY KEY COMMENT '联系人ID',
    user_id CHAR(10) NOT NULL COMMENT '所属用户ID',
    ct_name VARCHAR(20) NOT NULL COMMENT '姓名',
    ct_ad VARCHAR(100) COMMENT '地址',
    ct_yb VARCHAR(6) COMMENT '邮编',
    ct_qq VARCHAR(11) COMMENT 'QQ号',
    ct_wx VARCHAR(20) COMMENT '微信号',
    ct_em VARCHAR(50) COMMENT '邮箱',
    ct_mf CHAR(2) NOT NULL COMMENT '性别',
    ct_birth DATE COMMENT '出生日期',
    ct_phone CHAR(11) NOT NULL COMMENT '手机号',
    ct_delete INT DEFAULT 0 COMMENT '状态：0正常 1黑名单',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_ct_name (ct_name),
    INDEX idx_ct_phone (ct_phone),
    INDEX idx_ct_mf (ct_mf),
    INDEX idx_ct_delete (ct_delete),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

CREATE TABLE contact_pic (
    pic_id VARCHAR(10) PRIMARY KEY COMMENT '图片ID',
    ct_id CHAR(10) NOT NULL COMMENT '联系人ID',
    pic_name VARCHAR(255) NOT NULL COMMENT '图片文件名',
    pic_path VARCHAR(500) NOT NULL COMMENT '图片存储路径',
    pic_size INT COMMENT '图片大小（字节）',
    mime_type VARCHAR(50) COMMENT 'MIME类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ct_id) REFERENCES contact(ct_id) ON DELETE CASCADE,
    INDEX idx_ct_id (ct_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人头像表';

CREATE TABLE matter (
    matter_id VARCHAR(10) PRIMARY KEY COMMENT '事项ID',
    ct_id CHAR(10) NOT NULL COMMENT '联系人ID',
    user_id CHAR(10) NOT NULL COMMENT '所属用户ID',
    matter_time DATETIME NOT NULL COMMENT '事项时间',
    matter VARCHAR(100) NOT NULL COMMENT '事项内容',
    matter_delete INT DEFAULT 0 COMMENT '状态：0待完成 1已取消 2已完成',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ct_id) REFERENCES contact(ct_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    INDEX idx_ct_id (ct_id),
    INDEX idx_user_id (user_id),
    INDEX idx_matter_time (matter_time),
    INDEX idx_matter_delete (matter_delete),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人事项表';
