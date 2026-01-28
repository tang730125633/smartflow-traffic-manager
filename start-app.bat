@echo off
echo ========================================================
echo    Starting SmartFlow Traffic Manager
echo ========================================================
echo.

echo Checking if dependencies are installed...
python -c "import flask" >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Dependencies not installed!
    echo Please run: install-dependencies.bat
    pause
    exit /b 1
)

echo [OK] Dependencies found
echo.

echo Checking if database is initialized...
if not exist "instance\sftm.sqlite" (
    echo [WARNING] Database not found!
    echo Please run: init-database.bat
    pause
    exit /b 1
)

echo [OK] Database found
echo.

echo Starting application...
echo.
echo Access the application at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

python run_app.py

pause
