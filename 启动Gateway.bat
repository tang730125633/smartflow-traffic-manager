@echo off
chcp 65001 >nul
echo ════════════════════════════════════════
echo    启动 Gateway Service (API 网关)
echo ════════════════════════════════════════
echo.

cd spring-cloud-services\gateway-service

echo [1/2] 检查 Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Maven 未找到！请确保 Maven 已安装并配置环境变量
    pause
    exit /b 1
)
echo [✓] Maven 已就绪
echo.

echo [2/2] 启动 Gateway Service...
echo.
echo 提示：Gateway 会运行在端口 8080
echo       并自动注册到 Eureka (http://localhost:8761)
echo.
echo 正在启动，请稍候...
echo ════════════════════════════════════════
echo.

mvn spring-boot:run
