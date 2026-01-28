@echo off
title SmartFlow Traffic Manager
color 0A
echo ========================================================
echo    SmartFlow Traffic Manager
echo    Starting Application...
echo ========================================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [ERROR] Python is not installed!
    echo.
    echo Please install Python 3.8+ from:
    echo https://www.python.org/downloads/
    echo.
    echo During installation, check "Add Python to PATH"
    echo.
    pause
    exit /b 1
)

REM Check if dependencies are installed
python -c "import flask" >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing dependencies...
    echo This may take 5-10 minutes on first run...
    echo.
    pip install flask flask-cors werkzeug numpy pandas matplotlib requests pyyaml psutil -i https://pypi.tuna.tsinghua.edu.cn/simple --quiet
    if %errorlevel% neq 0 (
        color 0C
        echo.
        echo [ERROR] Failed to install dependencies
        echo.
        pause
        exit /b 1
    )
    echo [OK] Dependencies installed
)

REM Initialize database if needed
if not exist "instance\sftm.sqlite" (
    echo.
    echo Initializing database...
    python init_db.py
    if %errorlevel% neq 0 (
        color 0C
        echo.
        echo [ERROR] Database initialization failed
        echo.
        pause
        exit /b 1
    )
    echo [OK] Database initialized
)

REM Start the application
echo.
echo ========================================================
echo    Starting SmartFlow Traffic Manager...
echo ========================================================
echo.
echo Application will open in your browser:
echo http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.
echo ========================================================
echo.

python run_app.py

pause
