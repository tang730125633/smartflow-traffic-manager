@echo off
echo ========================================
echo Reset Admin Password
echo ========================================
echo.

echo This will update the admin user password to: admin123
echo.

echo Checking MySQL connection...
mysql -u root -proot -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Cannot connect to MySQL
    pause
    exit /b 1
)
echo [OK] MySQL connected
echo.

echo Updating admin password...
echo.

REM BCrypt hash for "admin123"
REM Generated with: BCrypt.hashpw("admin123", BCrypt.gensalt(10))
mysql -u root -proot smartflow_auth -e "UPDATE users SET password_hash='$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhUMNKJ7Ff.rCAC5Z.eIvQPYZj4i' WHERE username='admin';"

if %errorlevel% equ 0 (
    echo [OK] Password updated successfully!
    echo.
    echo You can now login with:
    echo   Username: admin
    echo   Password: admin123
    echo.
    echo IMPORTANT: You must restart Auth Service for this to work!
    echo   Run: restart-auth.bat
) else (
    echo [X] Failed to update password
)
echo.

echo Showing current admin user:
mysql -u root -proot smartflow_auth -e "SELECT id, username, email, role, is_active FROM users WHERE username='admin';"

echo.
pause
