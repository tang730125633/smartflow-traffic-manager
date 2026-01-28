#!/usr/bin/env python3
"""
SmartFlow Traffic Manager - 简化启动脚本
解决端口冲突问题，提供更好的用户体验
"""

import os
import sys
import socket
import subprocess
import time
from sftm_server import create_app

def kill_process_on_port(port):
    """杀死占用指定端口的进程"""
    try:
        # 查找占用端口的进程
        result = subprocess.run(['lsof', '-ti', f':{port}'], 
                              capture_output=True, text=True)
        if result.returncode == 0 and result.stdout.strip():
            pids = result.stdout.strip().split('\n')
            for pid in pids:
                if pid:
                    print(f"🔪 杀死进程 {pid} (占用端口 {port})")
                    subprocess.run(['kill', '-9', pid], check=False)
                    time.sleep(1)
            return True
    except Exception as e:
        print(f"⚠️  无法杀死进程: {e}")
    return False

def find_free_port(start_port=8080, max_port=8090):
    """查找可用端口"""
    for port in range(start_port, max_port + 1):
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.bind(('localhost', port))
                return port
        except OSError:
            continue
    return None

def check_port_usage(port):
    """检查端口是否被占用"""
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.settimeout(1)
            result = s.connect_ex(('localhost', port))
            return result == 0
    except:
        return False

def main():
    """启动Flask应用"""
    print("🚀 正在启动 SmartFlow Traffic Manager...")
    print("=" * 60)
    
    # 创建应用实例
    app = create_app()
    
    # 设置环境变量
    os.environ['FLASK_ENV'] = 'development'
    os.environ['FLASK_DEBUG'] = '1'
    
    # 检查端口占用情况
    default_port = 8080
    port = default_port
    
    if check_port_usage(default_port):
        print(f"⚠️  端口 {default_port} 已被占用")
        
        # 尝试杀死占用端口的进程
        if kill_process_on_port(default_port):
            print(f"✅ 已清理端口 {default_port}")
            time.sleep(2)
        else:
            print(f"🔍 正在查找可用端口...")
            free_port = find_free_port()
            if free_port:
                print(f"✅ 找到可用端口: {free_port}")
                port = free_port
            else:
                print("❌ 无法找到可用端口")
                print("请手动停止占用端口的程序，或指定其他端口")
                try:
                    port = int(input("请输入端口号 (默认8080): ") or "8080")
                except ValueError:
                    port = 8080
    else:
        print(f"✅ 端口 {port} 可用")
    
    print("✅ 应用配置完成")
    print(f"🌐 服务器将在 http://localhost:{port} 启动")
    print("📊 智慧交通管理平台已就绪")
    print("=" * 60)
    print("按 Ctrl+C 停止服务器")
    print()
    
    try:
        # 启动Flask开发服务器
        app.run(
            host='0.0.0.0',
            port=port,
            debug=True,
            threaded=True,
            use_reloader=False,  # 禁用热重载，避免端口冲突
        )
    except KeyboardInterrupt:
        print("\n👋 服务器已停止")
    except Exception as e:
        print(f"❌ 启动失败: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
