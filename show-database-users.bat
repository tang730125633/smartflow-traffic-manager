@echo off
echo ========================================
echo Show All Users in Database
echo ========================================
echo.

echo Connecting to MySQL...
mysql -u root -proot smartflow_auth -e "SELECT id, username, email, role, is_active, created_at FROM users;"

if %errorlevel% neq 0 (
    echo.
    echo [X] Failed to connect to database
    echo.
    echo Possible issues:
    echo   1. MySQL not running
    echo   2. Wrong password (should be: root)
    echo   3. Database not initialized
    echo.
    echo To fix:
    echo   Run: quick-fix.bat -> option 2
) else (
    echo.
    echo [OK] Database query successful
    echo.
    echo If you see users above, the database is OK.
    echo If no users shown, database needs to be initialized.
)
echo.

echo ========================================
echo Showing password hash for admin user:
echo ========================================
mysql -u root -proot smartflow_auth -e "SELECT username, password_hash FROM users WHERE username='admin';"
echo.

pause
