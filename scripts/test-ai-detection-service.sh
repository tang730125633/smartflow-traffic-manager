#!/bin/bash

# AI检测服务测试脚本

echo "测试AI检测服务..."

# 服务配置
SERVICE_URL="http://localhost:8005"
SERVICE_NAME="ai-detection-service"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试函数
test_endpoint() {
    local endpoint=$1
    local method=${2:-GET}
    local data=$3
    local description=$4
    
    echo -e "${YELLOW}测试: $description${NC}"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$SERVICE_URL$endpoint")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -d "$data" "$SERVICE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ]; then
        echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
        echo "响应: $body"
    else
        echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
        echo "响应: $body"
    fi
    echo ""
}

# 检查服务是否启动
echo "检查服务状态..."
if ! curl -s "$SERVICE_URL/api/health" > /dev/null; then
    echo -e "${RED}错误: AI检测服务未启动或无法访问${NC}"
    echo "请确保服务在端口8005上运行"
    exit 1
fi

echo -e "${GREEN}AI检测服务已启动${NC}"
echo ""

# 测试健康检查
test_endpoint "/api/health" "GET" "" "健康检查"
test_endpoint "/api/health/detailed" "GET" "" "详细健康检查"

# 测试交通数据接口
echo -e "${YELLOW}=== 测试交通数据接口 ===${NC}"

# 创建测试交通数据
traffic_data='{
    "dateTime": "2024-01-15T10:30:00",
    "pedestrianCount": 5,
    "carCount": 25,
    "bicycleCount": 8,
    "busCount": 3,
    "motorcycleCount": 12,
    "truckCount": 2,
    "pedestrianSpeed": 3.5,
    "carSpeed": 45.2,
    "bicycleSpeed": 15.8,
    "busSpeed": 35.6,
    "motorcycleSpeed": 38.9,
    "truckSpeed": 42.1,
    "volume": 1250.5,
    "congestionLevel": 1
}'

test_endpoint "/api/traffic-data" "POST" "$traffic_data" "保存交通数据"
test_endpoint "/api/traffic-data/latest" "GET" "" "获取最新交通数据"
test_endpoint "/api/traffic-data/congestion/1" "GET" "" "根据拥堵级别查询交通数据"

# 测试事故检测接口
echo -e "${YELLOW}=== 测试事故检测接口 ===${NC}"

# 创建测试事故检测数据
accident_data='{
    "dateTime": "2024-01-15T10:35:00",
    "imageData": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==",
    "involvedVehicles": "Car, Car",
    "confidenceScore": 0.92,
    "severity": "HIGH",
    "status": "PENDING",
    "location": "Intersection A",
    "description": "Two vehicles collision at main intersection"
}'

test_endpoint "/api/accident-detection" "POST" "$accident_data" "保存事故检测数据"
test_endpoint "/api/accident-detection/pending" "GET" "" "查询待处理事故"
test_endpoint "/api/accident-detection/high-confidence" "GET" "" "查询高置信度事故"
test_endpoint "/api/accident-detection/count" "GET" "" "统计事故数量"

# 测试时间范围查询
start_time="2024-01-15T00:00:00"
end_time="2024-01-15T23:59:59"

test_endpoint "/api/traffic-data/time-range?startTime=$start_time&endTime=$end_time" "GET" "" "时间范围查询交通数据"
test_endpoint "/api/accident-detection/time-range?startTime=$start_time&endTime=$end_time" "GET" "" "时间范围查询事故检测数据"

echo -e "${GREEN}=== 测试完成 ===${NC}"
echo "所有测试已完成，请检查上述结果"

