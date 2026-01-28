@echo off
echo ====================================
echo 启动 Auth Service (认证服务)
echo 端口: 8001
echo 请确保 Eureka Server 已启动！
echo ====================================
cd spring-cloud-services\auth-service
mvn spring-boot:run
pause
