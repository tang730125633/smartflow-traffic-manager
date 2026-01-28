@echo off
echo ========================================
echo Starting Auth Service
echo ========================================
echo.

echo [1/4] Checking if Eureka is running...
curl -s http://localhost:8761 > nul 2>&1
if %errorlevel% neq 0 (
    echo [X] ERROR: Eureka is not running!
    echo.
    echo Auth Service requires Eureka to be started first.
    echo Please start Eureka first by running: start-eureka.bat
    echo.
    pause
    exit /b 1
) else (
    echo [OK] Eureka is running
)
echo.

echo [2/4] Checking if Gateway is running...
curl -s http://localhost:8080 > nul 2>&1
if %errorlevel% neq 0 (
    echo [!] WARNING: Gateway is not running
    echo     You should start Gateway for the frontend to work
) else (
    echo [OK] Gateway is running
)
echo.

echo [3/4] Checking if port 8001 is available...
netstat -ano | findstr :8001 > nul 2>&1
if %errorlevel% equ 0 (
    echo [!] WARNING: Port 8001 is already in use
    echo.
    echo Trying to stop the process using port 8001...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8001 ^| findstr LISTENING') do (
        echo Stopping process %%a
        taskkill /PID %%a /F >nul 2>&1
    )
    echo Waiting 3 seconds...
    timeout /t 3 /nobreak >nul
) else (
    echo [OK] Port 8001 is available
)
echo.

echo [4/4] Starting Auth Service...
echo.
echo This window will show Auth Service logs.
echo DO NOT CLOSE this window.
echo.
echo Wait until you see: "Started AuthServiceApplication"
echo Then try logging in again.
echo.
echo Default login credentials:
echo   Username: admin
echo   Password: admin
echo.
echo ========================================
echo.

cd spring-cloud-services\auth-service
mvn spring-boot:run
