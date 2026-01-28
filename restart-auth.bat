@echo off
echo ========================================
echo Restart Auth Service with Fix
echo ========================================
echo.

echo [1/3] Stopping Auth Service...
echo.
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8001 ^| findstr LISTENING') do (
    echo Stopping process %%a on port 8001
    taskkill /PID %%a /F >nul 2>&1
)
echo Waiting 3 seconds...
timeout /t 3 /nobreak >nul
echo [OK] Auth Service stopped
echo.

echo [2/3] Cleaning old compiled files...
if exist "spring-cloud-services\auth-service\target" (
    echo Deleting target directory...
    rd /s /q "spring-cloud-services\auth-service\target"
    echo [OK] Target directory deleted
)
echo.

echo [3/3] Starting Auth Service with fixed code...
echo.
echo This window will show Auth Service logs.
echo DO NOT CLOSE this window.
echo.
echo Wait for: "Started AuthServiceApplication in X seconds"
echo Then try logging in again.
echo.
echo ========================================
echo.

cd spring-cloud-services\auth-service
mvn spring-boot:run
