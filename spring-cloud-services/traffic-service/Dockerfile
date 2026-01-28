FROM openjdk:11-jre-slim

# 安装OpenCV依赖
RUN apt-get update && apt-get install -y \
    libopencv-dev \
    python3-opencv \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/traffic-service-1.0.0.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "app.jar"]


