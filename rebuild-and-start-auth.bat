@echo off
echo ========================================
echo Rebuild and Start Auth Service
echo ========================================
echo.

echo [1/4] Changing to Auth Service directory...
cd spring-cloud-services\auth-service
if %errorlevel% neq 0 (
    echo [X] Failed to change directory
    pause
    exit /b 1
)
echo [OK] In auth-service directory
echo.

echo [2/4] Cleaning old build files...
if exist target (
    rd /s /q target
    echo [OK] Old build files deleted
) else (
    echo [!] No old build files found (OK)
)
echo.

echo [3/4] Compiling Auth Service (this may take 2-5 minutes)...
echo Downloading dependencies if needed...
echo.
mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo [X] BUILD FAILED
    echo     Look above for errors
    echo.
    pause
    exit /b 1
)
echo.
echo [OK] BUILD SUCCESS!
echo.

echo [4/4] Starting Auth Service...
echo.
echo This window will show Auth Service logs.
echo DO NOT CLOSE this window.
echo.
echo Wait for: "Started AuthServiceApplication in X seconds"
echo.
echo ========================================
echo.

mvn spring-boot:run

echo.
echo ========================================
echo Auth Service has stopped
echo.
pause
