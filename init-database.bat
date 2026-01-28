@echo off
echo ========================================================
echo    Initialize Database
echo ========================================================
echo.

echo Creating database tables...
python init_db.py

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Database initialized successfully!
    echo.
    echo You can now start the application.
) else (
    echo.
    echo [ERROR] Database initialization failed
    echo Please make sure Python and dependencies are installed.
    pause
    exit /b 1
)

echo.
pause
