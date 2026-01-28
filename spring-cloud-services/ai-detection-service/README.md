# AI检测服务 (AI Detection Service)

## 服务概述

AI检测服务是SmartFlow智慧交通综合管理平台的核心微服务之一，负责处理交通数据的AI分析和事故检测功能。

## 主要功能

### 1. 交通数据分析
- 实时交通流量统计
- 车辆类型识别和计数
- 速度分析和拥堵检测
- 历史数据查询和分析

### 2. 事故检测
- 基于AI的交通事故自动检测
- 事故严重程度评估
- 事故状态管理
- 检测结果存储和查询

### 3. 数据管理
- 交通数据CRUD操作
- 事故检测数据管理
- 数据缓存和优化
- 数据导出和统计

## 技术栈

- **框架**: Spring Boot 2.7.14
- **微服务**: Spring Cloud 2021.0.8
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **消息队列**: Apache Kafka
- **服务发现**: Eureka
- **API文档**: OpenAPI 3.0

## 项目结构

```
ai-detection-service/
├── src/main/java/com/smartflow/ai/
│   ├── AiDetectionServiceApplication.java    # 启动类
│   ├── config/                              # 配置类
│   │   ├── RedisConfig.java
│   │   └── KafkaConfig.java
│   ├── controller/                          # 控制器
│   │   ├── TrafficDataController.java
│   │   ├── AccidentDetectionController.java
│   │   └── HealthController.java
│   ├── dto/                                 # 数据传输对象
│   │   ├── TrafficDataDTO.java
│   │   └── AccidentDetectionDTO.java
│   ├── entity/                              # 实体类
│   │   ├── TrafficData.java
│   │   └── AccidentDetection.java
│   ├── repository/                          # 数据访问层
│   │   ├── TrafficDataRepository.java
│   │   └── AccidentDetectionRepository.java
│   ├── service/                             # 业务逻辑层
│   │   ├── TrafficDataService.java
│   │   └── AccidentDetectionService.java
│   ├── consumer/                            # Kafka消费者
│   │   └── TrafficDataConsumer.java
│   └── exception/                           # 异常处理
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   └── application.properties               # 配置文件
├── Dockerfile                               # Docker配置
├── docker-compose.yml                      # Docker Compose配置
└── pom.xml                                 # Maven配置
```

## 快速开始

### 1. 环境要求

- Java 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Kafka 2.8+

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE smartflow_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
source sql/ai_detection_init.sql
```

### 3. 配置修改

修改 `application.properties` 中的数据库、Redis、Kafka连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/smartflow_ai?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379

# Kafka配置
spring.kafka.bootstrap-servers=localhost:9092
```

### 4. 编译和运行

```bash
# 编译项目
mvn clean package

# 运行服务
java -jar target/ai-detection-service-1.0.0.jar

# 或使用启动脚本
./scripts/start-ai-detection-service.sh
```

### 5. Docker运行

```bash
# 构建镜像
docker build -t ai-detection-service .

# 运行容器
docker-compose up -d
```

## API接口

### 交通数据接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/traffic-data | 保存交通数据 |
| GET | /api/traffic-data/{id} | 根据ID查询交通数据 |
| GET | /api/traffic-data/time-range | 根据时间范围查询 |
| GET | /api/traffic-data/latest | 获取最新交通数据 |
| GET | /api/traffic-data/congestion/{level} | 根据拥堵级别查询 |
| GET | /api/traffic-data/average-volume | 获取平均交通量 |
| GET | /api/traffic-data/average-speed | 获取平均车速 |
| DELETE | /api/traffic-data/{id} | 删除交通数据 |

### 事故检测接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/accident-detection | 保存事故检测数据 |
| GET | /api/accident-detection/{id} | 根据ID查询事故检测数据 |
| GET | /api/accident-detection/time-range | 根据时间范围查询 |
| GET | /api/accident-detection/status/{status} | 根据状态查询 |
| GET | /api/accident-detection/severity/{severity} | 根据严重程度查询 |
| GET | /api/accident-detection/high-confidence | 查询高置信度事故 |
| GET | /api/accident-detection/pending | 查询待处理事故 |
| GET | /api/accident-detection/count | 统计事故数量 |
| PUT | /api/accident-detection/{id}/status | 更新事故状态 |
| DELETE | /api/accident-detection/{id} | 删除事故检测数据 |

### 健康检查接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/health | 基础健康检查 |
| GET | /api/health/detailed | 详细健康检查 |

## 配置说明

### 应用配置

```properties
# 服务配置
server.port=8005
spring.application.name=ai-detection-service

# AI检测配置
ai.detection.confidence-threshold=0.85
ai.detection.accident-cooldown=900
ai.detection.model-path=/app/models
ai.detection.video-path=/app/videos
```

### 数据库配置

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/smartflow_ai
spring.datasource.username=root
spring.datasource.password=root

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Redis配置

```properties
# Redis连接
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=2000ms
```

### Kafka配置

```properties
# Kafka连接
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=ai-detection-group
```

## 监控和日志

### 健康检查

服务提供健康检查端点，可以监控服务状态：

```bash
# 基础健康检查
curl http://localhost:8005/api/health

# 详细健康检查
curl http://localhost:8005/api/health/detailed
```

### 日志配置

日志级别可以在 `application.properties` 中配置：

```properties
# 日志配置
logging.level.com.smartflow.ai=DEBUG
logging.level.org.springframework.kafka=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 验证Redis连接配置
   - 检查网络连接

3. **Kafka连接失败**
   - 检查Kafka服务是否启动
   - 验证Kafka配置
   - 检查Zookeeper连接

4. **Eureka注册失败**
   - 检查Eureka服务是否启动
   - 验证Eureka配置
   - 检查网络连接

### 日志分析

查看服务日志：

```bash
# 查看应用日志
tail -f logs/ai-detection-service.log

# 查看错误日志
grep "ERROR" logs/ai-detection-service.log
```

## 开发指南

### 添加新功能

1. 创建实体类
2. 创建Repository接口
3. 创建Service类
4. 创建Controller类
5. 添加单元测试

### 代码规范

- 使用Lombok减少样板代码
- 遵循RESTful API设计原则
- 添加适当的日志记录
- 编写单元测试

## 版本历史

- **v1.0.0** - 初始版本，包含基础交通数据分析和事故检测功能

## 许可证

本项目采用MIT许可证。

