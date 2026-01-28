package com.smartflow.ai.controller;

import com.smartflow.ai.dto.TrafficInfoDto;
import com.smartflow.ai.model.AccidentDetection;
import com.smartflow.ai.model.TrafficData;
import com.smartflow.ai.service.AiDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI检测服务REST控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-detection")
@RequiredArgsConstructor
public class AiDetectionController {
    
    private final AiDetectionService aiDetectionService;
    
    /**
     * 开始视频检测
     */
    @PostMapping("/start")
    public ResponseEntity<String> startDetection() {
        try {
            aiDetectionService.startVideoDetection();
            return ResponseEntity.ok("Video detection started successfully");
        } catch (Exception e) {
            log.error("Error starting video detection", e);
            return ResponseEntity.internalServerError()
                .body("Failed to start video detection: " + e.getMessage());
        }
    }
    
    /**
     * 停止视频检测
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopDetection() {
        try {
            aiDetectionService.stopVideoDetection();
            return ResponseEntity.ok("Video detection stopped successfully");
        } catch (Exception e) {
            log.error("Error stopping video detection", e);
            return ResponseEntity.internalServerError()
                .body("Failed to stop video detection: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前交通信息
     */
    @GetMapping("/traffic/current")
    public ResponseEntity<TrafficInfoDto> getCurrentTrafficInfo() {
        try {
            TrafficInfoDto trafficInfo = aiDetectionService.getCurrentTrafficInfo();
            return ResponseEntity.ok(trafficInfo);
        } catch (Exception e) {
            log.error("Error getting current traffic info", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取历史交通数据
     */
    @GetMapping("/traffic/history")
    public ResponseEntity<List<TrafficData>> getHistoricalTrafficData(
            @RequestParam(defaultValue = "24") int hours) {
        try {
            List<TrafficData> trafficData = aiDetectionService.getHistoricalTrafficData(hours);
            return ResponseEntity.ok(trafficData);
        } catch (Exception e) {
            log.error("Error getting historical traffic data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取事故检测记录
     */
    @GetMapping("/accidents")
    public ResponseEntity<List<AccidentDetection>> getAccidentDetections(
            @RequestParam(defaultValue = "24") int hours) {
        try {
            List<AccidentDetection> accidents = aiDetectionService.getAccidentDetections(hours);
            return ResponseEntity.ok(accidents);
        } catch (Exception e) {
            log.error("Error getting accident detections", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 更新事故状态
     */
    @PutMapping("/accidents/{id}/status")
    public ResponseEntity<String> updateAccidentStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        try {
            aiDetectionService.updateAccidentStatus(id, status);
            return ResponseEntity.ok("Accident status updated successfully");
        } catch (Exception e) {
            log.error("Error updating accident status", e);
            return ResponseEntity.internalServerError()
                .body("Failed to update accident status: " + e.getMessage());
        }
    }
    
    /**
     * 处理单帧图像（用于测试）
     */
    @PostMapping("/process-frame")
    public ResponseEntity<TrafficInfoDto> processFrame(@RequestBody byte[] imageData) {
        try {
            TrafficInfoDto result = aiDetectionService.processFrame(imageData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing frame", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Detection Service is running");
    }
}

