#!/bin/bash

# SmartFlow 智慧交通管理平台 - 项目打包脚本
# 作者: AI Assistant
# 日期: 2024年9月18日

echo "🚀 开始打包 SmartFlow 智慧交通管理平台..."
echo "=================================================="

# 设置项目名称和版本
PROJECT_NAME="SmartFlow-Traffic-Manager"
VERSION="v1.0.0"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
PACKAGE_NAME="${PROJECT_NAME}_${VERSION}_${TIMESTAMP}"

echo "📦 项目名称: $PROJECT_NAME"
echo "📅 版本: $VERSION"
echo "⏰ 时间戳: $TIMESTAMP"
echo "📁 打包文件名: $PACKAGE_NAME.zip"
echo ""

# 检查是否在正确的目录
if [ ! -f "run_app.py" ]; then
    echo "❌ 错误: 请在项目根目录下运行此脚本"
    echo "   当前目录: $(pwd)"
    echo "   请确保在包含 run_app.py 的目录中运行"
    exit 1
fi

echo "✅ 确认在正确的项目目录中"
echo ""

# 创建临时打包目录
TEMP_DIR="/tmp/$PACKAGE_NAME"
echo "📁 创建临时目录: $TEMP_DIR"
mkdir -p "$TEMP_DIR"

# 复制项目文件（排除不需要的文件）
echo "📋 复制项目文件..."
rsync -av --progress \
    --exclude='venv/' \
    --exclude='__pycache__/' \
    --exclude='*.pyc' \
    --exclude='*.pyo' \
    --exclude='instance/sftm.sqlite' \
    --exclude='.git/' \
    --exclude='.DS_Store' \
    --exclude='*.log' \
    --exclude='*.tmp' \
    --exclude='.pytest_cache/' \
    --exclude='node_modules/' \
    . "$TEMP_DIR/"

echo "✅ 文件复制完成"
echo ""

# 创建压缩包
echo "🗜️  创建压缩包..."
cd /tmp
zip -r "${PACKAGE_NAME}.zip" "$PACKAGE_NAME" -x "*.DS_Store" "*/__pycache__/*" "*/venv/*"

# 检查压缩包是否创建成功
if [ -f "${PACKAGE_NAME}.zip" ]; then
    echo "✅ 压缩包创建成功: ${PACKAGE_NAME}.zip"
    
    # 显示压缩包信息
    PACKAGE_SIZE=$(du -h "${PACKAGE_NAME}.zip" | cut -f1)
    echo "📊 压缩包大小: $PACKAGE_SIZE"
    
    # 移动压缩包到项目目录
    mv "${PACKAGE_NAME}.zip" "$(dirname "$0")/"
    echo "📁 压缩包已移动到项目目录"
    
    # 清理临时目录
    rm -rf "$TEMP_DIR"
    echo "🧹 清理临时文件完成"
    
else
    echo "❌ 压缩包创建失败"
    exit 1
fi

echo ""
echo "🎉 打包完成！"
echo "=================================================="
echo "📦 压缩包文件: $PACKAGE_NAME.zip"
echo "📁 位置: $(dirname "$0")/$PACKAGE_NAME.zip"
echo "📊 大小: $PACKAGE_SIZE"
echo ""
echo "📋 包含内容:"
echo "   ✅ Flask 主应用代码"
echo "   ✅ Spring Cloud 微服务代码"
echo "   ✅ 前端模板和静态资源"
echo "   ✅ 数据库初始化脚本"
echo "   ✅ 配置文件和文档"
echo "   ✅ 启动脚本和说明文档"
echo ""
echo "📝 提交说明:"
echo "   1. 解压压缩包到任意目录"
echo "   2. 按照 SUBMISSION_GUIDE.md 中的说明启动项目"
echo "   3. 访问 http://localhost:8080 查看效果"
echo ""
echo "🔗 相关文档:"
echo "   - README.md: 项目概述和功能介绍"
echo "   - SUBMISSION_GUIDE.md: 详细启动指南"
echo "   - PROJECT_STRUCTURE.md: 项目结构说明"
echo "   - OPTIMIZATION_SUMMARY.md: 优化总结"
echo ""
echo "✨ 感谢使用 SmartFlow 智慧交通管理平台！"

