@echo off
chcp 65001 >nul
echo ========================================================
echo    SmartFlow Traffic Manager - Quick Start Wizard
echo ========================================================
echo.

echo [Step 1/5] Checking required software...
echo.

echo Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java not installed! Please install JDK 11+
    echo     Download: https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_windows-x64_bin.exe
    pause
    exit /b 1
) else (
    echo [OK] Java is installed
)

echo Checking Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Maven not installed!
    pause
    exit /b 1
) else (
    echo [OK] Maven is installed
)

echo Checking MySQL...
mysql -u root -proot -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] MySQL not running or password is not 'root'
    pause
    exit /b 1
) else (
    echo [OK] MySQL is running
)

echo Checking Redis...
redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Redis not running
    pause
    exit /b 1
) else (
    echo [OK] Redis is running
)

echo Checking Python...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Python not installed!
    pause
    exit /b 1
) else (
    echo [OK] Python is installed
)

echo.
echo [Step 2/5] Checking database...
echo.

mysql -u root -proot -e "USE smartflow_auth; USE smartflow_traffic;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Database not initialized, creating...
    mysql -u root -proot < sql\init.sql
    if %errorlevel% equ 0 (
        echo [OK] Database initialized successfully
    ) else (
        echo [X] Database initialization failed
        pause
        exit /b 1
    )
) else (
    echo [OK] Database exists
)

echo.
echo [Step 3/5] Checking Python dependencies...
echo.

python -c "import flask" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Python dependencies not installed, installing...
    pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
) else (
    echo [OK] Python dependencies installed
)

echo.
echo [Step 4/5] Select startup mode
echo.
echo Please select startup mode:
echo   1. Minimal mode (Eureka + Gateway + Flask frontend only)
echo   2. Standard mode (Eureka + Gateway + Auth + Flask frontend)
echo   3. Full mode (All services)
echo   4. Check environment only, don't start services
echo.
set /p choice="Please enter choice (1-4): "

if "%choice%"=="4" (
    echo.
    echo Environment check complete! All required software installed.
    echo.
    echo You can manually run these scripts to start services:
    echo   - start-eureka.bat    Start service registry
    echo   - start-gateway.bat   Start API gateway
    echo   - start-auth.bat      Start authentication service
    echo   - start-frontend.bat  Start frontend service
    echo.
    pause
    exit /b 0
)

echo.
echo [Step 5/5] Starting services...
echo.

if "%choice%"=="1" (
    echo Starting Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo Starting Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo Starting Flask frontend...
    start "Flask Frontend" cmd /k "python run_app.py"
)

if "%choice%"=="2" (
    echo Starting Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo Starting Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo Starting Auth Service...
    start "Auth Service" cmd /k "cd spring-cloud-services\auth-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo Starting Flask frontend...
    start "Flask Frontend" cmd /k "python run_app.py"
)

if "%choice%"=="3" (
    echo Starting Eureka Server...
    start "Eureka Server" cmd /k "cd spring-cloud-services\eureka-server && mvn spring-boot:run"
    timeout /t 30 /nobreak >nul

    echo Starting Gateway Service...
    start "Gateway Service" cmd /k "cd spring-cloud-services\gateway-service && mvn spring-boot:run"
    timeout /t 20 /nobreak >nul

    echo Starting Auth Service...
    start "Auth Service" cmd /k "cd spring-cloud-services\auth-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo Starting Traffic Service...
    start "Traffic Service" cmd /k "cd spring-cloud-services\traffic-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo Starting Analysis Service...
    start "Analysis Service" cmd /k "cd spring-cloud-services\analysis-service && mvn spring-boot:run"
    timeout /t 15 /nobreak >nul

    echo Starting Flask frontend...
    start "Flask Frontend" cmd /k "python run_app.py"
)

echo.
echo ========================================================
echo                  Startup Complete!
echo ========================================================
echo.
echo Please wait for all services to start (2-5 minutes)
echo.
echo Access URLs:
echo   Eureka Console: http://localhost:8761
echo   API Gateway:      http://localhost:8080
echo   Frontend:         http://localhost:8080 or http://localhost:5000
echo.
echo Tips: Each service runs in a separate window
echo        Close the window to stop the service
echo.
pause
