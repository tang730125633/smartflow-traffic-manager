@echo off
echo ========================================
echo Starting Gateway Service
echo ========================================
echo.

echo [1/3] Checking if Eureka is running...
curl -s http://localhost:8761 > nul 2>&1
if %errorlevel% neq 0 (
    echo [X] ERROR: Eureka is not running!
    echo.
    echo Gateway requires Eureka to be started first.
    echo Please start Eureka first by running: start-eureka.bat
    echo.
    pause
    exit /b 1
) else (
    echo [OK] Eureka is running
)
echo.

echo [2/3] Checking if port 8080 is available...
netstat -ano | findstr :8080 > nul 2>&1
if %errorlevel% equ 0 (
    echo [!] WARNING: Port 8080 is already in use
    echo.
    echo Trying to stop the process using port 8080...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        echo Stopping process %%a
        taskkill /PID %%a /F >nul 2>&1
    )
    echo Waiting 3 seconds...
    timeout /t 3 /nobreak >nul
) else (
    echo [OK] Port 8080 is available
)
echo.

echo [3/3] Starting Gateway Service...
echo.
echo This window will show Gateway logs.
echo DO NOT CLOSE this window.
echo.
echo Wait until you see: "Started GatewayServiceApplication"
echo Then test: http://localhost:8080
echo.
echo ========================================
echo.

cd spring-cloud-services\gateway-service
mvn spring-boot:run
