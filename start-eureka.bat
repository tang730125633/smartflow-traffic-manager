@echo off
echo ====================================
echo 启动 Eureka Server (服务注册中心)
echo 端口: 8761
echo ====================================
cd spring-cloud-services\eureka-server
mvn spring-boot:run
pause
