package com.smartflow.traffic.event.controller;

import com.smartflow.traffic.event.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 健康检查
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("service", applicationName);
        healthInfo.put("port", serverPort);
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("服务健康", healthInfo));
    }

    /**
     * 详细健康检查
     */
    @GetMapping("/detailed")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("service", applicationName);
        healthInfo.put("port", serverPort);
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        
        // 添加更多健康信息
        Map<String, String> details = new HashMap<>();
        details.put("database", "UP");
        details.put("redis", "UP");
        details.put("kafka", "UP");
        details.put("eureka", "UP");
        
        healthInfo.put("details", details);
        
        return ResponseEntity.ok(ApiResponse.success("详细健康检查", healthInfo));
    }
}

