# Zeabur 部署指南 - SmartFlow 简化版

## 📦 准备工作

### 1. 准备部署文件
```
spring-main-main/
├── sftm_server/          # Flask 应用（已有）
├── run_app.py           # 启动脚本（已有）
├── requirements-zeabur.txt   # 新建：简化的依赖
├── Dockerfile.zeabur    # 新建：Docker 配置
└── .zeabur.yml          # 新建：Zeabur 配置
```

### 2. 部署架构（简化版）

**只部署核心功能**：
- ✅ Flask 前端（仪表板、数据分析）
- ✅ Mock 数据生成器（无需 AI 模型）
- ✅ SQLite 数据库（内置）
- ✅ 用户认证系统

**移除的组件**：
- ❌ Spring Cloud 微服务（太复杂）
- ❌ AI 模型（YOLO、TensorFlow）
- ❌ 视频流处理
- ❌ Kafka 消息队列

---

## 🚀 部署步骤

### 方式 1：通过 Zeabur 网页部署（推荐）

1. **登录 Zeabur**
   - 访问 https://zeabur.com
   - 使用 GitHub 登录

2. **创建新项目**
   - 点击 "New Project"
   - 选择区域（推荐 Hong Kong 或 Tokyo）

3. **导入代码**
   - 选择 "Git"
   - 导入你的 GitHub 仓库
   - 或者直接上传文件夹

4. **配置服务**
   - Service Type: Python
   - Build Command: `pip install -r requirements-zeabur.txt`
   - Start Command: `python run_app.py`

5. **设置环境变量**
   ```
   FLASK_ENV=production
   PORT=8080
   ```

6. **部署**
   - 点击 "Deploy"
   - 等待 2-3 分钟

7. **获取访问地址**
   - 部署成功后会得到一个 URL
   - 例如: https://smartflow-traffic.zeabur.app

### 方式 2：使用 Dockerfile

1. 在 Zeabur 选择 "Docker" 模式
2. 使用 `Dockerfile.zeabur` 文件
3. 其余步骤同上

---

## 📊 部署后的功能

### 可用功能：
- ✅ 智能仪表板（实时数据展示）
- ✅ 交通分析（历史数据查询）
- ✅ 拥堵热力图
- ✅ 事故管理界面
- ✅ 信号控制界面
- ✅ 用户登录/注册
- ✅ 数据可视化（图表）

### 数据来源：
- 使用 Mock 数据生成器
- 自动生成模拟交通数据
- 无需真实摄像头和 AI 模型

---

## 💰 费用预估

### Zeabur 免费额度：
- $5.5/月 免费额度
- 包含：
  - 512MB 内存
  - 适量流量
  - 自动 HTTPS

### 预计使用量：
- 内存: ~200-300MB
- 流量: 视访问量而定
- **完全在免费额度内 ✅**

### 超出免费额度：
- 约 $0.01/小时 = $7.2/月
- 按实际使用量计费

---

## 🔧 自定义配置

### 修改默认数据生成频率

编辑 `sftm_server/mock_data_generator.py`：
```python
# 修改数据生成间隔（秒）
DATA_INTERVAL = 5  # 默认每5秒生成新数据
```

### 修改默认用户

首次访问 `/register` 注册管理员账号

---

## 🐛 故障排除

### 1. 部署失败
- 检查 `requirements-zeabur.txt` 是否正确
- 查看构建日志

### 2. 应用无法访问
- 确认 PORT 环境变量设置为 8080
- 检查 Zeabur 日志

### 3. 数据丢失
- Zeabur 重新部署会清空数据
- 建议定期导出 SQLite 数据库

### 4. 内存不足
- 在 Zeabur 控制台增加内存配额
- 或者移除不必要的依赖

---

## 📱 客户使用说明

### 访问地址
- 部署成功后分享 Zeabur 生成的 URL
- 例如: https://xxx.zeabur.app

### 账号注册
1. 访问 `/register`
2. 注册新用户
3. 使用注册的账号登录

### 功能演示
- 仪表板：自动显示实时交通数据
- 交通分析：查看历史数据和趋势
- 事故管理：查看事故记录（模拟数据）

---

## 🎯 下一步优化（可选）

### 如果客户需要完整功能：
1. **添加 AI 检测**：部署 YOLO 模型（需要更多资源）
2. **接入真实视频流**：配置摄像头或 RTMP 流
3. **数据持久化**：连接外部 MySQL 数据库
4. **多用户协作**：添加权限管理

### 此时费用预估：
- 内存: 1GB+
- 费用: $15-30/月

---

## ✅ 部署检查清单

- [ ] 已创建 `requirements-zeabur.txt`
- [ ] 已创建 `Dockerfile.zeabur`
- [ ] 已创建 `.zeabur.yml`
- [ ] 代码已推送到 GitHub
- [ ] Zeabur 账号已创建
- [ ] 服务已成功部署
- [ ] 可以访问应用 URL
- [ ] 已测试注册/登录功能

---

## 📞 需要帮助？

如果遇到问题，检查：
1. Zeabur 部署日志
2. 应用运行时日志
3. GitHub Issues

祝部署顺利！ 🎉
