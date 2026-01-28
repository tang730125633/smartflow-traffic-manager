@echo off
title Python Installation Guide
color 0E
echo ========================================================
echo    Python Installation Guide
echo ========================================================
echo.
echo Python is required to run this application.
echo.
echo STEP 1: Download Python
echo.
echo Open this link in your browser:
echo https://www.python.org/downloads/
echo.
echo Click "Download Python 3.x.x" button
echo.
echo ========================================================
echo.
pause

echo.
echo STEP 2: Install Python
echo.
echo IMPORTANT: During installation:
echo.
echo [ ] Check the box "Add Python to PATH"
echo [ ] Click "Install Now"
echo.
echo After installation completes, click "Close"
echo.
echo ========================================================
echo.
pause

echo.
echo STEP 3: Verify Installation
echo.
python --version
if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Python is installed!
    echo.
    echo You can now run: RUN-THIS.bat
) else (
    echo.
    echo [ERROR] Python is not installed yet
    echo.
    echo Please:
    echo 1. Download Python from the website
    echo 2. Install it
    echo 3. Restart your computer
    echo 4. Run this script again
)
echo.
echo ========================================================
pause
