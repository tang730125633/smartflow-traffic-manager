# SmartFlow Traffic Manager - Spring Cloud 微服务架构

## 项目概述

基于Spring Cloud的智慧交通综合管理平台，采用微服务架构设计，提供实时交通监控、数据分析、事故检测等功能。

## 架构设计

### 微服务组件

1. **Eureka Server** (8761) - 服务注册与发现中心
2. **Gateway Service** (8080) - API网关，统一入口
3. **Auth Service** (8001) - 认证授权服务
4. **Traffic Service** (8002) - 交通数据管理服务
5. **Analysis Service** (8003) - 数据分析服务
6. **Notification Service** (8004) - 通知服务
7. **Frontend Service** (3000) - 前端服务

### 技术栈

- **Spring Boot 2.7.18**
- **Spring Cloud 2021.0.8**
- **Spring Cloud Gateway** - API网关
- **Spring Cloud Netflix Eureka** - 服务注册发现
- **Spring Data JPA** - 数据访问
- **MySQL 8.0** - 主数据库
- **Redis** - 缓存
- **Apache Kafka** - 消息队列
- **OpenCV** - 计算机视觉处理
- **Docker** - 容器化部署

## 快速开始

### 环境要求

- Java 11+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0
- Redis

### 1. 克隆项目

```bash
git clone <repository-url>
cd SmartFlow-Traffic-Manager-main
```

### 2. 使用Docker Compose启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 访问服务

- **前端界面**: http://localhost:3000
- **API网关**: http://localhost:8080
- **Eureka控制台**: http://localhost:8761
- **服务健康检查**: http://localhost:8080/actuator/health

## 服务详细说明

### Eureka Server
- 端口: 8761
- 功能: 服务注册与发现
- 访问: http://localhost:8761

### Gateway Service
- 端口: 8080
- 功能: API网关，路由转发，负载均衡
- 路由配置:
  - `/auth/**` -> auth-service
  - `/traffic/**` -> traffic-service
  - `/analysis/**` -> analysis-service
  - `/notification/**` -> notification-service

### Auth Service
- 端口: 8001
- 功能: 用户认证、授权、JWT令牌管理
- 数据库: smartflow_auth

### Traffic Service
- 端口: 8002
- 功能: 实时视频流处理、目标检测、事故识别
- 数据库: smartflow_traffic
- 特性: OpenCV集成、实时处理

### Analysis Service
- 端口: 8003
- 功能: 交通数据统计、趋势分析、报告生成
- 数据库: smartflow_analysis

### Notification Service
- 端口: 8004
- 功能: 实时通知、消息推送、告警管理
- 数据库: smartflow_notification
- 特性: WebSocket支持

### Frontend Service
- 端口: 3000
- 功能: 用户界面、页面渲染
- 技术: Thymeleaf模板引擎

## 开发指南

### 本地开发

1. **启动基础设施**
```bash
# 启动MySQL、Redis、Kafka
docker-compose up mysql redis kafka zookeeper -d
```

2. **启动Eureka Server**
```bash
cd spring-cloud-services/eureka-server
mvn spring-boot:run
```

3. **启动其他服务**
```bash
# 在各自目录下运行
mvn spring-boot:run
```

### 构建和部署

```bash
# 构建所有服务
mvn clean package -DskipTests

# 构建Docker镜像
docker-compose build

# 启动所有服务
docker-compose up -d
```

## API文档

### 认证服务 API

```
POST /auth/login - 用户登录
POST /auth/register - 用户注册
POST /auth/refresh - 刷新令牌
GET /auth/user - 获取用户信息
```

### 交通数据服务 API

```
GET /traffic/data - 获取交通数据
POST /traffic/data - 创建交通数据
GET /traffic/accidents - 获取事故数据
POST /traffic/accidents - 报告事故
GET /traffic/stream - 实时视频流
```

### 分析服务 API

```
GET /analysis/reports - 获取分析报告
POST /analysis/generate - 生成分析报告
GET /analysis/statistics - 获取统计数据
GET /analysis/trends - 获取趋势数据
```

### 通知服务 API

```
GET /notification/list - 获取通知列表
POST /notification/send - 发送通知
PUT /notification/read - 标记已读
GET /notification/ws - WebSocket连接
```

## 配置说明

### 数据库配置

各服务使用独立的数据库：
- `smartflow_auth` - 认证服务
- `smartflow_traffic` - 交通数据服务
- `smartflow_analysis` - 分析服务
- `smartflow_notification` - 通知服务

### 环境变量

```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/database_name
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Redis配置
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Kafka配置
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Eureka配置
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
```

## 监控和运维

### 健康检查

所有服务都集成了Spring Boot Actuator，提供健康检查端点：

```bash
# 检查服务健康状态
curl http://localhost:8080/actuator/health

# 查看服务信息
curl http://localhost:8080/actuator/info
```

### 日志管理

```bash
# 查看特定服务日志
docker-compose logs -f traffic-service

# 查看所有服务日志
docker-compose logs -f
```

## 故障排除

### 常见问题

1. **端口冲突**
   - 检查端口是否被占用
   - 修改docker-compose.yml中的端口映射

2. **数据库连接失败**
   - 确保MySQL服务正在运行
   - 检查数据库连接配置

3. **服务注册失败**
   - 确保Eureka Server正在运行
   - 检查网络连接

4. **OpenCV依赖问题**
   - 确保Docker镜像包含OpenCV依赖
   - 检查模型文件路径

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

- 项目维护者: SmartFlow Team
- 邮箱: support@smartflow.com
- 项目地址: https://github.com/smartflow/traffic-manager
