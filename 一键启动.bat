@echo off
chcp 65001 >nul
echo ╔════════════════════════════════════════════════════╗
echo ║   SmartFlow 智慧交通管理平台 - Windows 启动向导    ║
echo ╚════════════════════════════════════════════════════╝
echo.

echo [步骤 1/5] 检查必备软件...
echo.

echo 检查 Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Java 未安装！请先安装 JDK 11
    echo     下载地址: https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_windows-x64_bin.exe
    pause
    exit /b 1
) else (
    echo [✓] Java 已安装
)

echo 检查 Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Maven 未安装！
    pause
    exit /b 1
) else (
    echo [✓] Maven 已安装
)

echo 检查 MySQL...
mysql -u root -proot -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] MySQL 未启动或密码不是 'root'
    pause
    exit /b 1
) else (
    echo [✓] MySQL 运行正常
)

echo 检查 Redis...
redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Redis 未启动
    pause
    exit /b 1
) else (
    echo [✓] Redis 运行正常
)

echo 检查 Python...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] Python 未安装！
    pause
    exit /b 1
) else (
    echo [✓] Python 已安装
)

echo.
echo [步骤 2/5] 检查数据库...
echo.

mysql -u root -proot -e "USE smartflow_auth; USE smartflow_traffic;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] 数据库未初始化，正在创建...
    mysql -u root -proot < sql\init.sql
    if %errorlevel% equ 0 (
        echo [✓] 数据库初始化成功
    ) else (
        echo [✗] 数据库初始化失败
        pause
        exit /b 1
    )
) else (
    echo [✓] 数据库已存在
)

echo.
echo [步骤 3/5] 检查 Python 依赖...
echo.

python -c "import flask" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Python 依赖未安装，正在安装...
    pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
) else (
    echo [✓] Python 依赖已安装
)

echo.
echo [步骤 4/5] 选择启动模式
echo.
echo 请选择启动模式：
echo   1. 最小模式（仅 Eureka + Gateway + Flask 前端）
echo   2. 标准模式（Eureka + Gateway + Auth + Flask 前端）
echo   3. 完整模式（所有服务）
echo   4. 仅检查环境，不启动服务
echo.
set /p choice="请输入选择 (1-4): "

if "%choice%"=="4" (
    echo.
    echo 环境检查完成！所有必备软件都已安装。
    echo.
    echo 你可以手动运行以下脚本启动服务：
    echo   - start-eureka.bat    启动服务注册中心
    echo   - start-gateway.bat   启动API网关
    echo   - start-auth.bat      启动认证服务
    echo   - start-frontend.bat  启动前端服务
    echo.
    pause
    exit /b 0
)

echo.
echo [步骤 5/5] 启动服务...
echo.

if "%choice%"=="1" (
    echo 启动 Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo 启动 Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo 启动 Flask 前端...
    start "Flask Frontend" cmd /k "python run_app.py"
)

if "%choice%"=="2" (
    echo 启动 Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo 启动 Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo 启动 Auth Service...
    start "Auth Service" cmd /k "cd spring-cloud-services\auth-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo 启动 Flask 前端...
    start "Flask Frontend" cmd /k "python run_app.py"
)

if "%choice%"=="3" (
    echo 启动 Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo 启动 Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo 启动 Auth Service...
    start "Auth Service" cmd /k "cd spring-cloud-services\auth-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo 启动 Traffic Service...
    start "Traffic Service" cmd /k "cd spring-cloud-services\traffic-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo 启动 Analysis Service...
    start "Analysis Service" cmd /k "cd spring-cloud-services\analysis-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo 启动 Flask 前端...
    start "Flask Frontend" cmd /k "python run_app.py"
)

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║                  启动完成！                         ║
echo ╚════════════════════════════════════════════════════╝
echo.
echo 请等待所有服务启动完成（约 2-5 分钟）
echo.
echo 访问地址：
echo   Eureka 控制台: http://localhost:8761
echo   API 网关:      http://localhost:8080
echo   前端界面:      http://localhost:8080 或 http://localhost:5000
echo.
echo 提示：每个服务会在独立的窗口中运行
echo       关闭窗口即可停止对应的服务
echo.
pause
