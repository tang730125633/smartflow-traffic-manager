package com.smartflow.ai.controller;

import com.smartflow.ai.dto.TrafficDataDTO;
import com.smartflow.ai.service.TrafficDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通数据控制器
 */
@RestController
@RequestMapping("/api/traffic-data")
@RequiredArgsConstructor
@Slf4j
public class TrafficDataController {
    
    private final TrafficDataService trafficDataService;
    
    /**
     * 保存交通数据
     */
    @PostMapping
    public ResponseEntity<TrafficDataDTO> saveTrafficData(@Valid @RequestBody TrafficDataDTO trafficDataDTO) {
        log.info("接收交通数据保存请求: {}", trafficDataDTO);
        TrafficDataDTO savedData = trafficDataService.saveTrafficData(trafficDataDTO);
        return ResponseEntity.ok(savedData);
    }
    
    /**
     * 根据ID查询交通数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrafficDataDTO> getTrafficDataById(@PathVariable Long id) {
        log.info("查询交通数据: {}", id);
        TrafficDataDTO trafficData = trafficDataService.getTrafficDataById(id);
        return ResponseEntity.ok(trafficData);
    }
    
    /**
     * 根据时间范围查询交通数据
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<TrafficDataDTO>> getTrafficDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("查询时间范围内的交通数据: {} - {}", startTime, endTime);
        List<TrafficDataDTO> trafficDataList = trafficDataService.getTrafficDataByTimeRange(startTime, endTime);
        return ResponseEntity.ok(trafficDataList);
    }
    
    /**
     * 获取最新的交通数据
     */
    @GetMapping("/latest")
    public ResponseEntity<List<TrafficDataDTO>> getLatestTrafficData() {
        log.info("查询最新交通数据");
        List<TrafficDataDTO> trafficDataList = trafficDataService.getLatestTrafficData();
        return ResponseEntity.ok(trafficDataList);
    }
    
    /**
     * 根据拥堵级别查询交通数据
     */
    @GetMapping("/congestion/{level}")
    public ResponseEntity<List<TrafficDataDTO>> getTrafficDataByCongestionLevel(@PathVariable Integer level) {
        log.info("查询拥堵级别为 {} 的交通数据", level);
        List<TrafficDataDTO> trafficDataList = trafficDataService.getTrafficDataByCongestionLevel(level);
        return ResponseEntity.ok(trafficDataList);
    }
    
    /**
     * 获取平均交通量
     */
    @GetMapping("/average-volume")
    public ResponseEntity<Double> getAverageVolume(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("查询平均交通量: {} - {}", startTime, endTime);
        Double averageVolume = trafficDataService.getAverageVolume(startTime, endTime);
        return ResponseEntity.ok(averageVolume);
    }
    
    /**
     * 获取平均车速
     */
    @GetMapping("/average-speed")
    public ResponseEntity<Double> getAverageCarSpeed(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("查询平均车速: {} - {}", startTime, endTime);
        Double averageSpeed = trafficDataService.getAverageCarSpeed(startTime, endTime);
        return ResponseEntity.ok(averageSpeed);
    }
    
    /**
     * 删除交通数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrafficData(@PathVariable Long id) {
        log.info("删除交通数据: {}", id);
        trafficDataService.deleteTrafficData(id);
        return ResponseEntity.ok().build();
    }
}

