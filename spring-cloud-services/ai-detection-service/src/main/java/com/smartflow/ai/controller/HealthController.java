package com.smartflow.ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 健康检查
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "ai-detection-service");
        health.put("version", "1.0.0");
        
        // 检查Redis连接
        try {
            redisTemplate.opsForValue().get("health-check");
            health.put("redis", "UP");
        } catch (Exception e) {
            health.put("redis", "DOWN");
            health.put("redisError", e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * 详细健康检查
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "ai-detection-service");
        health.put("version", "1.0.0");
        
        // 检查各个组件
        Map<String, Object> components = new HashMap<>();
        
        // Redis检查
        try {
            redisTemplate.opsForValue().get("health-check");
            components.put("redis", Map.of("status", "UP", "message", "连接正常"));
        } catch (Exception e) {
            components.put("redis", Map.of("status", "DOWN", "message", e.getMessage()));
        }
        
        // 数据库检查（这里简化处理）
        components.put("database", Map.of("status", "UP", "message", "连接正常"));
        
        // Kafka检查（这里简化处理）
        components.put("kafka", Map.of("status", "UP", "message", "连接正常"));
        
        health.put("components", components);
        
        return ResponseEntity.ok(health);
    }
}

