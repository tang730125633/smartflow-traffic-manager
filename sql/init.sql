-- SmartFlow Traffic Manager 数据库初始化脚本
-- 创建所有微服务所需的数据库

-- 创建认证服务数据库
CREATE DATABASE IF NOT EXISTS smartflow_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建交通数据服务数据库
CREATE DATABASE IF NOT EXISTS smartflow_traffic CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建分析服务数据库
CREATE DATABASE IF NOT EXISTS smartflow_analysis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建通知服务数据库
CREATE DATABASE IF NOT EXISTS smartflow_notification CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建AI检测服务数据库
CREATE DATABASE IF NOT EXISTS smartflow_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用认证服务数据库
USE smartflow_auth;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'OPERATOR', 'VIEWER') DEFAULT 'VIEWER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入默认管理员用户
INSERT INTO users (username, email, password_hash, role) VALUES 
('admin', 'admin@smartflow.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

-- 使用交通数据服务数据库
USE smartflow_traffic;

-- 创建交通数据表
CREATE TABLE IF NOT EXISTS traffic_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    pedestrian_count INT DEFAULT 0,
    car_count INT DEFAULT 0,
    bicycle_count INT DEFAULT 0,
    bus_count INT DEFAULT 0,
    motorcycle_count INT DEFAULT 0,
    truck_count INT DEFAULT 0,
    pedestrian_speed DECIMAL(5,2) DEFAULT 0.00,
    car_speed DECIMAL(5,2) DEFAULT 0.00,
    bicycle_speed DECIMAL(5,2) DEFAULT 0.00,
    bus_speed DECIMAL(5,2) DEFAULT 0.00,
    motorcycle_speed DECIMAL(5,2) DEFAULT 0.00,
    truck_speed DECIMAL(5,2) DEFAULT 0.00,
    volume DECIMAL(5,2) DEFAULT 0.00,
    congestion INT DEFAULT 0,
    camera_id VARCHAR(100),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_date_time (date_time),
    INDEX idx_camera_id (camera_id),
    INDEX idx_location (location)
);

-- 创建事故表
CREATE TABLE IF NOT EXISTS accidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    image LONGBLOB,
    involved TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL', 'UNKNOWN') DEFAULT 'UNKNOWN',
    status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'CANCELLED') DEFAULT 'PENDING',
    location VARCHAR(255),
    description TEXT,
    confidence_score DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_date_time (date_time),
    INDEX idx_severity (severity),
    INDEX idx_status (status)
);

-- 使用分析服务数据库
USE smartflow_analysis;

-- 创建分析报告表
CREATE TABLE IF NOT EXISTS analysis_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    parameters JSON,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    INDEX idx_report_type (report_type),
    INDEX idx_generated_at (generated_at)
);

-- 使用通知服务数据库
USE smartflow_notification;

-- 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('INFO', 'WARNING', 'ERROR', 'SUCCESS') DEFAULT 'INFO',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    is_read BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- 创建系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入默认配置
INSERT INTO system_config (config_key, config_value, description) VALUES 
('system.name', 'SmartFlow Traffic Manager', '系统名称'),
('system.version', '2.0.0', '系统版本'),
('traffic.analysis.interval', '300', '交通分析间隔（秒）'),
('notification.retention.days', '30', '通知保留天数'),
('accident.auto.detect', 'true', '自动事故检测开关')
ON DUPLICATE KEY UPDATE config_value=VALUES(config_value);

