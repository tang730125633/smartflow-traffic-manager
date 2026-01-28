import random
import time
from datetime import datetime, timedelta
import numpy as np

class MockDataGenerator:
    def __init__(self):
        self.base_traffic_levels = {
            'person': 15,
            'bicycle': 8,
            'car': 45,
            'motorcycle': 12,
            'bus': 6,
            'truck': 4
        }
        self.base_speeds = {
            'person': 5,
            'bicycle': 15,
            'car': 35,
            'motorcycle': 40,
            'bus': 25,
            'truck': 30
        }
        
    def generate_traffic_data(self):
        """生成模拟交通数据"""
        current_hour = datetime.now().hour
        
        # 根据时间调整交通量（早晚高峰）
        if 7 <= current_hour <= 9 or 17 <= current_hour <= 19:
            multiplier = 1.8  # 高峰时段
        elif 10 <= current_hour <= 16:
            multiplier = 1.2  # 白天
        elif 20 <= current_hour <= 22:
            multiplier = 0.8  # 晚上
        else:
            multiplier = 0.3  # 深夜/凌晨
        
        traffic_info = {}
        for vehicle_type in self.base_traffic_levels:
            # 添加随机变化
            base_count = self.base_traffic_levels[vehicle_type]
            variation = random.uniform(0.7, 1.3)
            count = int(base_count * multiplier * variation)
            
            base_speed = self.base_speeds[vehicle_type]
            speed_variation = random.uniform(0.8, 1.2)
            speed = base_speed * speed_variation
            
            traffic_info[vehicle_type] = [count, speed]
        
        return traffic_info
    
    def generate_accident_data(self):
        """生成模拟事故数据"""
        # 5%的概率发生事故
        if random.random() < 0.05:
            accident_types = ['Bike, Bike', 'Bike, Object', 'Bike, Pedestrian', 
                            'Car, Bike', 'Car, Car', 'Car, Object', 'Car, Pedestrian']
            return {
                'involved': random.choice(accident_types),
                'timestamp': datetime.now().isoformat()
            }
        return None
    
    def generate_hourly_data(self):
        """生成24小时数据用于图表显示"""
        hourly_data = {}
        for hour in range(24):
            # 模拟一天中的交通模式
            if 7 <= hour <= 9 or 17 <= hour <= 19:
                multiplier = 1.5  # 高峰
            elif 10 <= hour <= 16:
                multiplier = 1.0  # 正常
            elif 20 <= hour <= 22:
                multiplier = 0.7  # 晚上
            else:
                multiplier = 0.3  # 深夜
            
            hour_data = {}
            for vehicle_type in self.base_traffic_levels:
                base_count = self.base_traffic_levels[vehicle_type]
                count = int(base_count * multiplier * random.uniform(0.8, 1.2))
                hour_data[vehicle_type] = count
            
            hourly_data[hour] = hour_data
        
        return hourly_data

# 全局模拟数据生成器实例
mock_generator = MockDataGenerator()

