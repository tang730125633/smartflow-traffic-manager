package com.smartflow.orchestration.controller;

import com.smartflow.orchestration.entity.TrafficSignal;
import com.smartflow.orchestration.service.SignalSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 信号调度控制器
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/signals")
@RequiredArgsConstructor
public class SignalSchedulingController {
    
    private final SignalSchedulingService signalSchedulingService;
    
    /**
     * 获取所有信号灯状态
     */
    @GetMapping
    public ResponseEntity<List<TrafficSignal>> getAllSignals() {
        log.info("获取所有信号灯状态");
        List<TrafficSignal> signals = signalSchedulingService.getAllSignals();
        return ResponseEntity.ok(signals);
    }
    
    /**
     * 根据ID获取信号灯
     */
    @GetMapping("/{signalId}")
    public ResponseEntity<TrafficSignal> getSignalById(@PathVariable String signalId) {
        log.info("获取信号灯: {}", signalId);
        TrafficSignal signal = signalSchedulingService.getSignalById(signalId);
        return ResponseEntity.ok(signal);
    }
    
    /**
     * 更新信号灯状态
     */
    @PutMapping("/{signalId}/status")
    public ResponseEntity<TrafficSignal> updateSignalStatus(
            @PathVariable String signalId,
            @RequestParam TrafficSignal.SignalStatus status,
            @RequestParam(required = false) Integer remainingTime) {
        
        log.info("更新信号灯状态: {} -> {}", signalId, status);
        TrafficSignal signal = signalSchedulingService.updateSignalStatus(signalId, status, remainingTime);
        return ResponseEntity.ok(signal);
    }
    
    /**
     * 执行信号调度
     */
    @PostMapping("/scheduling/execute")
    public ResponseEntity<String> executeScheduling() {
        log.info("执行信号调度");
        signalSchedulingService.executeScheduling();
        return ResponseEntity.ok("信号调度执行完成");
    }
    
    /**
     * 根据交通流量调整信号灯
     */
    @PostMapping("/{signalId}/adjust/traffic")
    public ResponseEntity<String> adjustSignalByTrafficVolume(
            @PathVariable String signalId,
            @RequestParam Integer trafficVolume) {
        
        log.info("根据交通流量调整信号灯: {} 流量: {}", signalId, trafficVolume);
        signalSchedulingService.adjustSignalByTrafficVolume(signalId, trafficVolume);
        return ResponseEntity.ok("信号灯调整完成");
    }
    
    /**
     * 根据事件调整信号灯
     */
    @PostMapping("/{signalId}/adjust/incident")
    public ResponseEntity<String> adjustSignalByIncident(
            @PathVariable String signalId,
            @RequestParam String incidentType,
            @RequestParam Integer severity) {
        
        log.info("根据事件调整信号灯: {} 事件类型: {} 严重程度: {}", signalId, incidentType, severity);
        signalSchedulingService.adjustSignalByIncident(signalId, incidentType, severity);
        return ResponseEntity.ok("信号灯调整完成");
    }
    
    /**
     * 执行紧急调度
     */
    @PostMapping("/{signalId}/emergency")
    public ResponseEntity<String> executeEmergencyScheduling(@PathVariable String signalId) {
        log.warn("执行紧急调度: {}", signalId);
        signalSchedulingService.executeEmergencyScheduling(signalId);
        return ResponseEntity.ok("紧急调度执行完成");
    }
    
    /**
     * 获取信号灯统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSignalStatistics() {
        log.info("获取信号灯统计信息");
        Map<String, Object> statistics = signalSchedulingService.getSignalStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 批量更新信号灯
     */
    @PutMapping("/batch")
    public ResponseEntity<String> batchUpdateSignals(@RequestBody List<TrafficSignal> signals) {
        log.info("批量更新信号灯: {} 个", signals.size());
        signalSchedulingService.batchUpdateSignals(signals);
        return ResponseEntity.ok("批量更新完成");
    }
    
    /**
     * 启用/禁用自动调度
     */
    @PutMapping("/{signalId}/auto-scheduling")
    public ResponseEntity<String> toggleAutoScheduling(
            @PathVariable String signalId,
            @RequestParam Boolean enabled) {
        
        log.info("切换自动调度: {} -> {}", signalId, enabled);
        signalSchedulingService.toggleAutoScheduling(signalId, enabled);
        return ResponseEntity.ok("自动调度状态已更新");
    }
}
