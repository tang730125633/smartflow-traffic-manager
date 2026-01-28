@echo off
echo ====================================
echo 检查服务状态
echo ====================================
echo.
echo 检查 MySQL...
mysql -u root -proot -e "SELECT 'MySQL 运行正常' AS Status;" 2>nul
if %errorlevel% neq 0 echo [错误] MySQL 未启动或密码不正确

echo.
echo 检查 Redis...
redis-cli ping 2>nul
if %errorlevel% neq 0 echo [错误] Redis 未启动

echo.
echo 检查 Java...
java -version 2>nul
if %errorlevel% neq 0 echo [错误] Java 未安装或未配置环境变量

echo.
echo 检查 Maven...
mvn -version 2>nul
if %errorlevel% neq 0 echo [错误] Maven 未安装或未配置环境变量

echo.
echo 检查 Python...
python --version 2>nul
if %errorlevel% neq 0 echo [错误] Python 未安装或未配置环境变量

echo.
echo 检查端口占用...
echo 8761 端口 (Eureka):
netstat -ano | findstr :8761
echo 8080 端口 (Gateway):
netstat -ano | findstr :8080

echo.
pause
