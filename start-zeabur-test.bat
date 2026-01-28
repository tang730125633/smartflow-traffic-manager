@echo off
echo ====================================
echo 测试 Zeabur 简化版本
echo ====================================
echo.

echo [1] 检查 Python...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Python 未安装！
    pause
    exit /b 1
)
echo [OK] Python 已安装

echo.
echo [2] 安装简化的依赖...
pip install flask flask-cors werkzeug numpy pandas -i https://pypi.tuna.tsinghua.edu.cn/simple

echo.
echo [3] 启动应用...
python run_app.py

pause
