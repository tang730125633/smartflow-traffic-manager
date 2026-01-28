package com.smartflow.ai.controller;

import com.smartflow.ai.dto.AccidentDetectionDTO;
import com.smartflow.ai.service.AccidentDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 事故检测控制器
 */
@RestController
@RequestMapping("/api/accident-detection")
@RequiredArgsConstructor
@Slf4j
public class AccidentDetectionController {
    
    private final AccidentDetectionService accidentDetectionService;
    
    /**
     * 保存事故检测数据
     */
    @PostMapping
    public ResponseEntity<AccidentDetectionDTO> saveAccidentDetection(@Valid @RequestBody AccidentDetectionDTO accidentDetectionDTO) {
        log.info("接收事故检测数据保存请求: {}", accidentDetectionDTO);
        AccidentDetectionDTO savedData = accidentDetectionService.saveAccidentDetection(accidentDetectionDTO);
        return ResponseEntity.ok(savedData);
    }
    
    /**
     * 根据ID查询事故检测数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccidentDetectionDTO> getAccidentDetectionById(@PathVariable Long id) {
        log.info("查询事故检测数据: {}", id);
        AccidentDetectionDTO accidentDetection = accidentDetectionService.getAccidentDetectionById(id);
        return ResponseEntity.ok(accidentDetection);
    }
    
    /**
     * 根据时间范围查询事故检测数据
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<AccidentDetectionDTO>> getAccidentDetectionByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("查询时间范围内的事故检测数据: {} - {}", startTime, endTime);
        List<AccidentDetectionDTO> accidentDetectionList = accidentDetectionService.getAccidentDetectionByTimeRange(startTime, endTime);
        return ResponseEntity.ok(accidentDetectionList);
    }
    
    /**
     * 根据状态查询事故检测数据
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccidentDetectionDTO>> getAccidentDetectionByStatus(@PathVariable String status) {
        log.info("查询状态为 {} 的事故检测数据", status);
        List<AccidentDetectionDTO> accidentDetectionList = accidentDetectionService.getAccidentDetectionByStatus(status);
        return ResponseEntity.ok(accidentDetectionList);
    }
    
    /**
     * 根据严重程度查询事故检测数据
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AccidentDetectionDTO>> getAccidentDetectionBySeverity(@PathVariable String severity) {
        log.info("查询严重程度为 {} 的事故检测数据", severity);
        List<AccidentDetectionDTO> accidentDetectionList = accidentDetectionService.getAccidentDetectionBySeverity(severity);
        return ResponseEntity.ok(accidentDetectionList);
    }
    
    /**
     * 查询高置信度的事故检测数据
     */
    @GetMapping("/high-confidence")
    public ResponseEntity<List<AccidentDetectionDTO>> getHighConfidenceAccidents(
            @RequestParam(defaultValue = "0.8") Double threshold) {
        log.info("查询置信度大于 {} 的事故检测数据", threshold);
        List<AccidentDetectionDTO> accidentDetectionList = accidentDetectionService.getHighConfidenceAccidents(threshold);
        return ResponseEntity.ok(accidentDetectionList);
    }
    
    /**
     * 查询待处理的事故检测数据
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AccidentDetectionDTO>> getPendingAccidents() {
        log.info("查询待处理的事故检测数据");
        List<AccidentDetectionDTO> accidentDetectionList = accidentDetectionService.getPendingAccidents();
        return ResponseEntity.ok(accidentDetectionList);
    }
    
    /**
     * 统计指定时间范围内的事故数量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAccidentsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("统计时间范围内的事故数量: {} - {}", startTime, endTime);
        Long count = accidentDetectionService.countAccidentsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 更新事故检测状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AccidentDetectionDTO> updateAccidentStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        log.info("更新事故检测状态: {} -> {}", id, status);
        AccidentDetectionDTO updatedData = accidentDetectionService.updateAccidentStatus(id, status);
        return ResponseEntity.ok(updatedData);
    }
    
    /**
     * 删除事故检测数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccidentDetection(@PathVariable Long id) {
        log.info("删除事故检测数据: {}", id);
        accidentDetectionService.deleteAccidentDetection(id);
        return ResponseEntity.ok().build();
    }
}

