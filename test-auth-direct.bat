@echo off
echo ========================================
echo Testing Auth Service Directly
echo ========================================
echo.

echo [1] Checking if Auth Service is running on port 8001...
netstat -ano | findstr :8001
if %errorlevel% neq 0 (
    echo [X] Port 8001 NOT in use - Auth Service is NOT running!
    echo.
    echo Please run: start-auth-only.bat
    pause
    exit /b 1
) else (
    echo [OK] Port 8001 in use
)
echo.

echo [2] Checking database for users...
echo.
mysql -u root -proot smartflow_auth -e "SELECT id, username, email, role, is_active FROM users;"
if %errorlevel% neq 0 (
    echo [X] Cannot query database
    pause
    exit /b 1
)
echo.

echo [3] Testing Auth Service health endpoint...
echo.
curl -s http://localhost:8001/actuator/health
echo.
echo.

echo [4] Testing direct login to Auth Service (bypassing Gateway)...
echo.
echo Testing with username: admin, password: admin
curl -X POST http://localhost:8001/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin\"}"
echo.
echo.

echo [5] Testing via Gateway...
echo.
echo Testing with username: admin, password: admin
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin\"}"
echo.
echo.

echo ========================================
echo.
echo Check the responses above:
echo   - If you see a token or success message, login is working
echo   - If you see "invalid credentials", password is wrong
echo   - If you see "404" or "503", routing is broken
echo   - If you see connection error, service not running
echo.
pause
