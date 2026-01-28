#!/usr/bin/env python3
"""初始化数据库"""

from sftm_server import create_app
from sftm_server.db import init_db

app = create_app()

with app.app_context():
    init_db()
    print("✅ 数据库初始化成功！")
    print("✅ 表已创建：user, traffic, accidents, incidents")
