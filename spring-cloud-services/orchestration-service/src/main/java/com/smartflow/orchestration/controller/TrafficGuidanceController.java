package com.smartflow.orchestration.controller;

import com.smartflow.orchestration.entity.TrafficGuidance;
import com.smartflow.orchestration.service.TrafficGuidanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 交通诱导控制器
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/guidance")
@RequiredArgsConstructor
public class TrafficGuidanceController {
    
    private final TrafficGuidanceService guidanceService;
    
    /**
     * 创建诱导信息
     */
    @PostMapping
    public ResponseEntity<TrafficGuidance> createGuidance(@RequestBody TrafficGuidance guidance) {
        log.info("创建交通诱导信息: {}", guidance.getTitle());
        TrafficGuidance createdGuidance = guidanceService.createGuidance(guidance);
        return ResponseEntity.ok(createdGuidance);
    }
    
    /**
     * 更新诱导信息
     */
    @PutMapping("/{guidanceId}")
    public ResponseEntity<TrafficGuidance> updateGuidance(
            @PathVariable Long guidanceId,
            @RequestBody TrafficGuidance guidance) {
        
        log.info("更新交通诱导信息: {}", guidanceId);
        TrafficGuidance updatedGuidance = guidanceService.updateGuidance(guidanceId, guidance);
        return ResponseEntity.ok(updatedGuidance);
    }
    
    /**
     * 删除诱导信息
     */
    @DeleteMapping("/{guidanceId}")
    public ResponseEntity<String> deleteGuidance(@PathVariable Long guidanceId) {
        log.info("删除交通诱导信息: {}", guidanceId);
        guidanceService.deleteGuidance(guidanceId);
        return ResponseEntity.ok("诱导信息删除成功");
    }
    
    /**
     * 获取所有诱导信息
     */
    @GetMapping
    public ResponseEntity<List<TrafficGuidance>> getAllGuidance() {
        log.info("获取所有交通诱导信息");
        List<TrafficGuidance> guidanceList = guidanceService.getAllGuidance();
        return ResponseEntity.ok(guidanceList);
    }
    
    /**
     * 获取当前有效的诱导信息
     */
    @GetMapping("/valid")
    public ResponseEntity<List<TrafficGuidance>> getValidGuidance() {
        log.info("获取当前有效的诱导信息");
        List<TrafficGuidance> guidanceList = guidanceService.getValidGuidance();
        return ResponseEntity.ok(guidanceList);
    }
    
    /**
     * 根据类型获取诱导信息
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TrafficGuidance>> getGuidanceByType(
            @PathVariable TrafficGuidance.GuidanceType type) {
        
        log.info("根据类型获取诱导信息: {}", type);
        List<TrafficGuidance> guidanceList = guidanceService.getGuidanceByType(type);
        return ResponseEntity.ok(guidanceList);
    }
    
    /**
     * 根据区域获取诱导信息
     */
    @GetMapping("/area/{area}")
    public ResponseEntity<List<TrafficGuidance>> getGuidanceByArea(@PathVariable String area) {
        log.info("根据区域获取诱导信息: {}", area);
        List<TrafficGuidance> guidanceList = guidanceService.getGuidanceByArea(area);
        return ResponseEntity.ok(guidanceList);
    }
    
    /**
     * 启用/禁用诱导信息
     */
    @PutMapping("/{guidanceId}/toggle")
    public ResponseEntity<String> toggleGuidance(
            @PathVariable Long guidanceId,
            @RequestParam Boolean enabled) {
        
        log.info("切换诱导信息状态: {} -> {}", guidanceId, enabled);
        guidanceService.toggleGuidance(guidanceId, enabled);
        return ResponseEntity.ok("诱导信息状态已更新");
    }
    
    /**
     * 获取诱导信息统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getGuidanceStatistics() {
        log.info("获取诱导信息统计");
        Map<String, Object> statistics = guidanceService.getGuidanceStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 批量创建诱导信息
     */
    @PostMapping("/batch")
    public ResponseEntity<String> batchCreateGuidance(@RequestBody List<TrafficGuidance> guidanceList) {
        log.info("批量创建诱导信息: {} 个", guidanceList.size());
        guidanceService.batchCreateGuidance(guidanceList);
        return ResponseEntity.ok("批量创建完成");
    }
    
    /**
     * 清理过期诱导信息
     */
    @PostMapping("/clean-expired")
    public ResponseEntity<String> cleanExpiredGuidance() {
        log.info("清理过期诱导信息");
        guidanceService.cleanExpiredGuidance();
        return ResponseEntity.ok("过期诱导信息清理完成");
    }
    
    /**
     * 根据优先级获取诱导信息
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TrafficGuidance>> getGuidanceByPriority(@PathVariable Integer priority) {
        log.info("根据优先级获取诱导信息: {}", priority);
        List<TrafficGuidance> guidanceList = guidanceService.getGuidanceByPriority(priority);
        return ResponseEntity.ok(guidanceList);
    }
    
    /**
     * 更新诱导信息显示次数
     */
    @PutMapping("/{guidanceId}/display-count")
    public ResponseEntity<String> updateDisplayCount(@PathVariable Long guidanceId) {
        log.debug("更新诱导信息显示次数: {}", guidanceId);
        guidanceService.updateDisplayCount(guidanceId);
        return ResponseEntity.ok("显示次数已更新");
    }
}
