@echo off
chcp 65001 >nul
echo ════════════════════════════════════════
echo    启动 Auth Service (认证服务)
echo ════════════════════════════════════════
echo.

cd spring-cloud-services\auth-service

echo [1/2] 检查 Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Maven 未找到！请确保 Maven 已安装并配置环境变量
    pause
    exit /b 1
)
echo [✓] Maven 已就绪
echo.

echo [2/2] 启动 Auth Service...
echo.
echo 提示：Auth Service 负责用户登录认证
echo       端口：8001
echo       会自动注册到 Eureka (http://localhost:8761)
echo.
echo 正在启动，请稍候...
echo ════════════════════════════════════════
echo.

mvn spring-boot:run
