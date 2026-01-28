#!/bin/bash

# AI检测服务启动脚本

echo "启动AI检测服务..."

# 设置Java环境
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-11-openjdk}
export PATH=$JAVA_HOME/bin:$PATH

# 设置服务配置
SERVICE_NAME="ai-detection-service"
SERVICE_PORT=8005
JAR_FILE="spring-cloud-services/ai-detection-service/target/ai-detection-service-1.0.0.jar"

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请确保已安装Java 11或更高版本"
    exit 1
fi

# 检查JAR文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 未找到JAR文件 $JAR_FILE"
    echo "请先执行 mvn clean package 编译项目"
    exit 1
fi

# 检查端口是否被占用
if lsof -Pi :$SERVICE_PORT -sTCP:LISTEN -t >/dev/null ; then
    echo "错误: 端口 $SERVICE_PORT 已被占用"
    exit 1
fi

# 启动服务
echo "启动 $SERVICE_NAME 在端口 $SERVICE_PORT..."
java -jar $JAR_FILE \
    --spring.profiles.active=dev \
    --server.port=$SERVICE_PORT \
    --spring.datasource.url=jdbc:mysql://localhost:3306/smartflow_ai?useSSL=false&serverTimezone=UTC \
    --spring.datasource.username=root \
    --spring.datasource.password=root \
    --eureka.client.service-url.defaultZone=http://localhost:8761/eureka/ \
    --spring.redis.host=localhost \
    --spring.redis.port=6379 \
    --spring.kafka.bootstrap-servers=localhost:9092

echo "$SERVICE_NAME 已启动完成"

