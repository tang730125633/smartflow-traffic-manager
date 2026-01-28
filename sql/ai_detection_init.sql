-- AI检测服务数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smartflow_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smartflow_ai;

-- 交通数据表
CREATE TABLE IF NOT EXISTS traffic_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time DATETIME NOT NULL,
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
    volume DECIMAL(10,2) DEFAULT 0.00,
    congestion_level INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_date_time (date_time),
    INDEX idx_congestion_level (congestion_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 事故检测表
CREATE TABLE IF NOT EXISTS accident_detection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time DATETIME NOT NULL,
    image_data LONGBLOB,
    involved_vehicles VARCHAR(255),
    confidence_score DECIMAL(3,2) NOT NULL,
    severity VARCHAR(50) DEFAULT 'Medium',
    status VARCHAR(50) DEFAULT 'Pending',
    location VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_date_time (date_time),
    INDEX idx_status (status),
    INDEX idx_confidence_score (confidence_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入一些示例数据
INSERT INTO traffic_data (date_time, pedestrian_count, car_count, bicycle_count, bus_count, motorcycle_count, truck_count, 
                         pedestrian_speed, car_speed, bicycle_speed, bus_speed, motorcycle_speed, truck_speed, 
                         volume, congestion_level) VALUES
(NOW() - INTERVAL 1 HOUR, 5, 25, 8, 3, 12, 2, 3.5, 45.2, 15.8, 35.6, 38.9, 42.1, 1250.5, 1),
(NOW() - INTERVAL 30 MINUTE, 3, 18, 6, 2, 9, 1, 4.2, 52.1, 18.3, 38.9, 42.5, 45.8, 980.3, 0),
(NOW() - INTERVAL 15 MINUTE, 7, 32, 12, 4, 15, 3, 3.8, 38.7, 12.5, 32.1, 35.2, 38.9, 1450.8, 2);

-- 插入一些示例事故数据
INSERT INTO accident_detection (date_time, involved_vehicles, confidence_score, severity, status, location, description) VALUES
(NOW() - INTERVAL 2 HOUR, 'Car, Car', 0.92, 'High', 'Resolved', 'Intersection A', 'Two vehicles collision at main intersection'),
(NOW() - INTERVAL 1 HOUR, 'Car, Bicycle', 0.87, 'Medium', 'Pending', 'Street B', 'Vehicle and bicycle accident'),
(NOW() - INTERVAL 30 MINUTE, 'Bike, Bike', 0.89, 'Low', 'Investigating', 'Park Road', 'Two bicycles collision');

