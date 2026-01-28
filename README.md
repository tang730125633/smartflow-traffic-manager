# SmartFlow 智慧交通综合管理平台

基于Spring Cloud微服务架构的智慧交通综合管理平台，提供实时交通监控、数据分析、事故检测、信号控制等功能。

## 🚀 项目特色

- **实时监控**: 基于OpenCV的实时视频流处理和目标检测
- **智能分析**: AI驱动的交通数据分析和预测
- **事故检测**: 自动识别交通事故并实时告警
- **信号控制**: 动态调整交通信号灯时序
- **微服务架构**: 基于Spring Cloud的可扩展架构
- **现代化UI**: 响应式Web界面，支持多设备访问

## 📋 功能模块

### 🏠 智能仪表板
- 实时交通数据展示
- 车辆和行人计数统计
- 拥堵区域热力图
- 事故分布地图

### 📊 交通分析
- 历史数据查询和分析
- 交通流量趋势预测
- 多维度数据统计
- 自定义时间范围分析

### 🚦 信号控制
- 交通信号灯状态监控
- 动态信号时序调整
- 路口流量优化
- 信号配时方案管理

### 🛣️ 诱导管理
- 交通诱导信息发布
- 路线推荐和导航
- 实时路况更新
- 多语言支持

### 🚨 事故管理
- 实时事故检测和告警
- 事故位置精确定位
- 应急响应流程
- 事故统计分析

## 🛠️ 技术栈

### 后端技术
- **Spring Boot 2.7.18** - 微服务框架
- **Spring Cloud 2021.0.8** - 微服务治理
- **Spring Cloud Gateway** - API网关
- **Spring Cloud Netflix Eureka** - 服务注册发现
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 主数据库
- **Redis** - 缓存和会话存储
- **Apache Kafka** - 消息队列

### 前端技术
- **Flask** - Web框架
- **Bootstrap 5** - UI框架
- **Chart.js** - 数据可视化
- **ApexCharts** - 高级图表
- **jQuery** - JavaScript库

### AI/ML技术
- **OpenCV** - 计算机视觉
- **YOLOv8** - 目标检测
- **TensorFlow** - 机器学习框架
- **PyTorch** - 深度学习框架

### 部署技术
- **Docker** - 容器化
- **Docker Compose** - 多容器编排
- **Maven** - 项目构建

## 🚀 快速开始

### 环境要求

- **Java 11+**
- **Python 3.8+**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **MySQL 8.0**
- **Redis**

### 1. 克隆项目

```bash
git clone <repository-url>
cd SmartFlow-Traffic-Manager-main
```

### 2. 启动服务

#### 方式一：Docker Compose（推荐）

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 方式二：本地开发

```bash
# 1. 激活Python虚拟环境
source venv/bin/activate

# 2. 安装Python依赖
pip install -r requirements.txt

# 3. 启动前端服务
python run_app.py
```

### 3. 访问应用

- **主页面**: http://localhost:8080
- **仪表板**: http://localhost:8080/dashboard
- **交通分析**: http://localhost:8080/traffic_analysis
- **Eureka控制台**: http://localhost:8761

## 📁 项目结构

```
SmartFlow-Traffic-Manager-main/
├── sftm_server/                 # Flask前端服务
│   ├── static/                 # 静态资源
│   ├── templates/              # HTML模板
│   ├── db.py                   # 数据库操作
│   └── mock_data_generator.py  # 模拟数据生成器
├── spring-cloud-services/       # Spring Cloud微服务
│   ├── eureka-server/          # 服务注册中心
│   ├── gateway-service/        # API网关
│   ├── auth-service/           # 认证服务
│   ├── traffic-service/        # 交通数据服务
│   ├── analysis-service/       # 数据分析服务
│   ├── notification-service/   # 通知服务
│   └── ai-detection-service/   # AI检测服务
├── sql/                        # 数据库脚本
├── scripts/                    # 启动脚本
├── requirements.txt            # Python依赖
├── docker-compose.yml          # Docker编排文件
└── run_app.py                  # 应用启动入口
```

## 🔧 配置说明

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

## 📊 API文档

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

## 🐛 故障排除

### 常见问题

1. **端口冲突**
   ```bash
   # 检查端口占用
   lsof -i :8080
   
   # 杀死占用进程
   kill -9 <PID>
   ```

2. **数据库连接失败**
   - 确保MySQL服务正在运行
   - 检查数据库连接配置
   - 验证数据库用户权限

3. **服务注册失败**
   - 确保Eureka Server正在运行
   - 检查网络连接
   - 验证服务配置

4. **Python依赖问题**
   ```bash
   # 重新安装依赖
   pip install -r requirements.txt --force-reinstall
   ```

## 📈 监控和运维

### 健康检查

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

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- **项目维护者**: SmartFlow Team
- **邮箱**: support@smartflow.com
- **项目地址**: https://github.com/smartflow/traffic-manager

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和开源社区。

---

**注意**: 这是一个学术研究项目，主要用于展示智慧交通管理系统的设计和实现。在生产环境中使用前，请确保进行充分的安全性和性能测试。