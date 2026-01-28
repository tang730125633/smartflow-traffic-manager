@echo off
echo ========================================================
echo    Install Python Dependencies
echo ========================================================
echo.

echo Checking Python installation...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python is not installed!
    echo Please install Python 3.8+ from: https://www.python.org/downloads/
    pause
    exit /b 1
)

echo [OK] Python is installed
echo.
echo Installing dependencies...
echo This may take 5-10 minutes...
echo.

pip install flask flask-cors werkzeug numpy pandas matplotlib requests pyyaml psutil -i https://pypi.tuna.tsinghua.edu.cn/simple

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Dependencies installed successfully!
) else (
    echo.
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)

echo.
pause
