# 事件管理服务Dockerfile
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apk add --no-cache curl

# 复制jar文件
COPY target/event-management-service-*.jar app.jar

# 暴露端口
EXPOSE 8006

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8006/api/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

