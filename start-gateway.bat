@echo off
echo ====================================
echo 启动 Gateway Service (API网关)
echo 端口: 8080
echo 请确保 Eureka Server 已启动！
echo ====================================
cd spring-cloud-services\gateway-service
mvn spring-boot:run
pause
