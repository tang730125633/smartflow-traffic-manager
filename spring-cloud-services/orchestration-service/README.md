# SmartFlow 调度与诱导服务

## 服务概述

调度与诱导服务是SmartFlow智慧交通管理平台的核心服务之一，负责交通信号灯的智能调度、规则引擎自动执行和交通诱导信息管理。

## 主要功能

### 1. 信号调度功能
- 交通信号灯状态管理
- 实时信号调度
- 根据交通流量自动调整
- 根据事件自动响应
- 紧急调度模式

### 2. 规则引擎
- 调度规则管理
- 规则条件评估
- 规则动作执行
- 规则优先级控制
- 规则执行历史

### 3. 交通诱导
- 诱导信息管理
- 多类型诱导支持
- 区域化诱导
- 优先级控制
- 有效期管理

## 技术栈

- **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.1**
- **Spring Data JPA**
- **MySQL 8.0**
- **Redis 7**
- **Apache Kafka**
- **Eureka Client**

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7+
- Apache Kafka 2.8+

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE smartflow_orchestration CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 启动服务

```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

### 4. 服务端口

- **HTTP端口**: 8007
- **健康检查**: http://localhost:8007/actuator/health

## API接口

### 信号调度API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/signals` | 获取所有信号灯状态 |
| GET | `/api/signals/{signalId}` | 根据ID获取信号灯 |
| PUT | `/api/signals/{signalId}/status` | 更新信号灯状态 |
| POST | `/api/signals/scheduling/execute` | 执行信号调度 |
| POST | `/api/signals/{signalId}/adjust/traffic` | 根据交通流量调整 |
| POST | `/api/signals/{signalId}/adjust/incident` | 根据事件调整 |
| POST | `/api/signals/{signalId}/emergency` | 执行紧急调度 |

### 规则引擎API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/rules` | 创建调度规则 |
| PUT | `/api/rules/{ruleId}` | 更新调度规则 |
| DELETE | `/api/rules/{ruleId}` | 删除调度规则 |
| GET | `/api/rules` | 获取所有规则 |
| GET | `/api/rules/enabled` | 获取启用的规则 |
| POST | `/api/rules/evaluate` | 执行规则评估 |
| POST | `/api/rules/{ruleId}/execute` | 执行指定规则 |

### 交通诱导API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/guidance` | 创建诱导信息 |
| PUT | `/api/guidance/{guidanceId}` | 更新诱导信息 |
| DELETE | `/api/guidance/{guidanceId}` | 删除诱导信息 |
| GET | `/api/guidance` | 获取所有诱导信息 |
| GET | `/api/guidance/valid` | 获取当前有效的诱导信息 |
| GET | `/api/guidance/type/{type}` | 根据类型获取诱导信息 |
| GET | `/api/guidance/area/{area}` | 根据区域获取诱导信息 |

## 配置说明

### 应用配置 (application.yml)

```yaml
server:
  port: 8007

spring:
  application:
    name: orchestration-service
  
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/smartflow_orchestration
    username: root
    password: 123456
    
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      database: 2
      
  # Kafka配置
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: orchestration-service-group

# 调度服务配置
orchestration:
  signal:
    default-cycle-time: 120
    min-green-time: 15
    max-green-time: 60
  rule-engine:
    enabled: true
    evaluation-interval: 30
  guidance:
    enabled: true
    update-interval: 60
```

## 监控指标

服务提供以下监控指标：

- **健康检查**: `/actuator/health`
- **应用信息**: `/actuator/info`
- **指标数据**: `/actuator/metrics`
- **Prometheus指标**: `/actuator/prometheus`

## 日志配置

日志级别配置：
- `com.smartflow.orchestration`: DEBUG
- `org.springframework.kafka`: INFO
- `org.hibernate.SQL`: DEBUG

## 开发指南

### 添加新的调度规则

1. 在`SchedulingRule`实体中定义规则类型
2. 在`RuleEngineService`中实现规则逻辑
3. 在`SignalSchedulingService`中集成规则执行

### 添加新的诱导类型

1. 在`TrafficGuidance.GuidanceType`枚举中添加新类型
2. 在`TrafficGuidanceService`中实现相关逻辑
3. 更新API接口和文档

## 故障排除

### 常见问题

1. **服务启动失败**
   - 检查数据库连接配置
   - 检查Redis连接配置
   - 检查Kafka连接配置

2. **规则执行失败**
   - 检查规则条件格式
   - 检查规则动作格式
   - 查看日志中的错误信息

3. **信号调度异常**
   - 检查信号灯数据完整性
   - 检查调度规则配置
   - 查看调度日志

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

MIT License
