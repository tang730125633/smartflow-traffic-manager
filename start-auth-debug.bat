@echo off
echo ========================================
echo Start Auth Service - Debug Mode
echo ========================================
echo.

echo Changing to Auth Service directory...
cd spring-cloud-services\auth-service
echo Current directory: %CD%
echo.

echo Checking if pom.xml exists...
if exist pom.xml (
    echo [OK] pom.xml found
) else (
    echo [X] pom.xml NOT found - wrong directory!
    echo.
    pause
    exit /b 1
)
echo.

echo Starting Auth Service with Maven...
echo This window will stay open so you can see any errors.
echo.
echo ========================================
echo.

mvn spring-boot:run

echo.
echo ========================================
echo Auth Service has stopped or failed to start
echo.
echo Look above for ERROR messages or Exceptions
echo.
echo Common issues:
echo   - "Connection refused" = Eureka not running
echo   - "Port already in use" = Another process using port 8001
echo   - "Could not resolve dependencies" = Maven download failed
echo   - "Access denied" = MySQL connection failed
echo.
pause
