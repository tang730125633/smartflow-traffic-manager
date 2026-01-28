@echo off
title SmartFlow Traffic Manager - Setup
color 0B
mode con lines=50 cols=100

echo ================================================================
echo                SmartFlow Traffic Manager
echo                    One-Click Setup
echo ================================================================
echo.
echo This will check your system and install everything needed
echo.
echo Press any key to continue or close this window to cancel...
pause >nul

REM Check Python
echo.
echo [1/4] Checking Python installation...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo [!] Python not found. Downloading installer...
    echo.

    REM Open Python download page
    start https://www.python.org/ftp/python/3.11.8/python-3.11.8-amd64.exe

    echo.
    echo ================================================================
    echo                PYTHON INSTALLATION REQUIRED
    echo ================================================================
    echo.
    echo STEP-BY-STEP INSTRUCTIONS:
    echo.
    echo 1. A download has started (python-3.11.8-amd64.exe)
    echo 2. When download completes, RUN the installer
    echo 3. IMPORTANT: Check "Add Python to PATH" at the bottom
    echo 4. Click "Install Now"
    echo 5. Wait for installation to complete
    echo 6. Click "Close" when done
    echo 7. RETURN TO THIS WINDOW
    echo 8. Press any key to continue
    echo.
    echo ================================================================
    echo.
    pause

    REM Verify Python installation
    python --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo.
        echo [ERROR] Python still not detected!
        echo.
        echo Please make sure:
        echo - You installed Python
        echo - You checked "Add Python to PATH"
        echo - You restarted this window
        echo.
        pause
        exit /b 1
    )
)

echo [OK] Python is installed

REM Check pip
echo.
echo [2/4] Checking pip...
python -m pip --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing pip...
    python -m ensurepip --upgrade
)
echo [OK] pip is ready

REM Install dependencies
echo.
echo [3/4] Installing application dependencies...
echo This may take 5-10 minutes on first run...
echo Please wait...
echo.

python -m pip install flask flask-cors werkzeug numpy pandas matplotlib requests pyyaml psutil -i https://pypi.tuna.tsinghua.edu.cn/simple --quiet

if %errorlevel% neq 0 (
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)
echo [OK] Dependencies installed

REM Initialize database
echo.
echo [4/4] Initializing database...
if not exist "instance\sftm.sqlite" (
    python init_db.py
    if %errorlevel% neq 0 (
        echo [ERROR] Database initialization failed
        pause
        exit /b 1
    )
)
echo [OK] Database ready

echo.
echo ================================================================
echo                  SETUP COMPLETE!
echo ================================================================
echo.
echo Press any key to start the application...
pause >nul

REM Start application
call RUN-THIS.bat
