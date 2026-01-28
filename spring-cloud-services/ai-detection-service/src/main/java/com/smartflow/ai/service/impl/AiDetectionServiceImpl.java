package com.smartflow.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartflow.ai.dto.TrafficInfoDto;
import com.smartflow.ai.model.AccidentDetection;
import com.smartflow.ai.model.TrafficData;
import com.smartflow.ai.repository.AccidentDetectionRepository;
import com.smartflow.ai.repository.TrafficDataRepository;
import com.smartflow.ai.service.AiDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI检测服务实现类
 * 简化版本，模拟AI检测功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiDetectionServiceImpl implements AiDetectionService {
    
    private final TrafficDataRepository trafficDataRepository;
    private final AccidentDetectionRepository accidentDetectionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.detection.confidence-threshold:0.85}")
    private double confidenceThreshold;
    
    @Value("${ai.detection.accident-cooldown:900}")
    private int accidentCooldown;
    
    private final AtomicBoolean isDetectionRunning = new AtomicBoolean(false);
    private LocalDateTime lastAccidentTime = LocalDateTime.now().minusHours(1);
    
    // 模拟的车辆类型映射
    private final Map<Integer, String> ID2TRAFFIC_CLASS = Map.of(
        0, "person", 1, "bicycle", 2, "car", 3, "motorcycle", 
        5, "bus", 7, "truck"
    );
    
    private final Map<Integer, String> ID2ACCIDENT_CLASS = Map.of(
        0, "Bike, Bike", 1, "Bike, Object", 2, "Bike, Pedestrian",
        3, "Car, Bike", 4, "Car, Car", 5, "Car, Object", 6, "Car, Pedestrian"
    );
    
    @PostConstruct
    public void init() {
        log.info("AI Detection Service initialized");
    }
    
    @Override
    @Async
    public void startVideoDetection() {
        if (isDetectionRunning.compareAndSet(false, true)) {
            log.info("Starting video detection...");
            // 模拟视频检测过程
            simulateVideoDetection();
        } else {
            log.warn("Video detection is already running");
        }
    }
    
    @Override
    public void stopVideoDetection() {
        if (isDetectionRunning.compareAndSet(true, false)) {
            log.info("Stopping video detection...");
        } else {
            log.warn("Video detection is not running");
        }
    }
    
    @Override
    public TrafficInfoDto processFrame(byte[] imageData) {
        // 模拟AI处理，生成随机交通数据
        TrafficInfoDto trafficInfo = generateMockTrafficData();
        
        // 保存到数据库
        saveTrafficData(trafficInfo);
        
        // 发送到Kafka
        sendTrafficDataToKafka(trafficInfo);
        
        return trafficInfo;
    }
    
    @Override
    public AccidentDetection detectAccident(byte[] imageData) {
        // 检查事故冷却时间
        if (LocalDateTime.now().isBefore(lastAccidentTime.plusSeconds(accidentCooldown))) {
            return null;
        }
        
        // 模拟事故检测（随机生成）
        if (Math.random() < 0.01) { // 1% 概率检测到事故
            AccidentDetection accident = generateMockAccident(imageData);
            accidentDetectionRepository.save(accident);
            lastAccidentTime = LocalDateTime.now();
            
            // 发送事故通知到Kafka
            sendAccidentNotificationToKafka(accident);
            
            return accident;
        }
        
        return null;
    }
    
    @Override
    public TrafficInfoDto getCurrentTrafficInfo() {
        // 从Redis或数据库获取最新数据
        return generateMockTrafficData();
    }
    
    @Override
    public List<TrafficData> getHistoricalTrafficData(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return trafficDataRepository.findByDateTimeAfter(startTime);
    }
    
    @Override
    public List<AccidentDetection> getAccidentDetections(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return accidentDetectionRepository.findByDateTimeAfter(startTime);
    }
    
    @Override
    public void updateAccidentStatus(Long accidentId, String status) {
        Optional<AccidentDetection> accidentOpt = accidentDetectionRepository.findById(accidentId);
        if (accidentOpt.isPresent()) {
            AccidentDetection accident = accidentOpt.get();
            accident.setStatus(status);
            accidentDetectionRepository.save(accident);
            log.info("Updated accident {} status to {}", accidentId, status);
        }
    }
    
    private void simulateVideoDetection() {
        new Thread(() -> {
            while (isDetectionRunning.get()) {
                try {
                    // 模拟处理视频帧
                    processFrame(new byte[0]);
                    
                    // 模拟事故检测
                    detectAccident(new byte[0]);
                    
                    Thread.sleep(2000); // 2秒间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in video detection simulation", e);
                }
            }
        }).start();
    }
    
    private TrafficInfoDto generateMockTrafficData() {
        Map<String, TrafficInfoDto.VehicleInfo> vehicles = new HashMap<>();
        
        // 生成随机车辆数据
        vehicles.put("person", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 10), Math.random() * 5
        ));
        vehicles.put("car", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 50), Math.random() * 30 + 20
        ));
        vehicles.put("bicycle", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 15), Math.random() * 15 + 10
        ));
        vehicles.put("bus", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 5), Math.random() * 20 + 15
        ));
        vehicles.put("motorcycle", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 20), Math.random() * 25 + 15
        ));
        vehicles.put("truck", new TrafficInfoDto.VehicleInfo(
            (int) (Math.random() * 8), Math.random() * 25 + 15
        ));
        
        // 计算总流量和拥堵等级
        double totalVolume = vehicles.values().stream()
            .mapToDouble(v -> v.getCount() * v.getAverageSpeed())
            .sum();
        
        int congestionLevel = calculateCongestionLevel(totalVolume);
        String[] congestionLevels = {"Low", "Medium", "High"};
        
        return new TrafficInfoDto(vehicles, totalVolume, congestionLevel, congestionLevels[congestionLevel]);
    }
    
    private int calculateCongestionLevel(double volume) {
        if (volume < 100) return 0; // Low
        if (volume < 200) return 1; // Medium
        return 2; // High
    }
    
    private AccidentDetection generateMockAccident(byte[] imageData) {
        AccidentDetection accident = new AccidentDetection();
        accident.setDateTime(LocalDateTime.now());
        accident.setImageData(imageData);
        accident.setInvolvedVehicles(getRandomAccidentType());
        accident.setConfidenceScore(0.85 + Math.random() * 0.14);
        accident.setSeverity("Medium");
        accident.setStatus("Pending");
        accident.setLocation("Intersection A");
        accident.setDescription("Vehicle collision detected");
        
        return accident;
    }
    
    private String getRandomAccidentType() {
        List<String> accidentTypes = new ArrayList<>(ID2ACCIDENT_CLASS.values());
        return accidentTypes.get((int) (Math.random() * accidentTypes.size()));
    }
    
    private void saveTrafficData(TrafficInfoDto trafficInfo) {
        try {
            TrafficData trafficData = new TrafficData();
            trafficData.setDateTime(LocalDateTime.now());
            
            Map<String, TrafficInfoDto.VehicleInfo> vehicles = trafficInfo.getVehicles();
            trafficData.setPedestrianCount(vehicles.get("person").getCount());
            trafficData.setCarCount(vehicles.get("car").getCount());
            trafficData.setBicycleCount(vehicles.get("bicycle").getCount());
            trafficData.setBusCount(vehicles.get("bus").getCount());
            trafficData.setMotorcycleCount(vehicles.get("motorcycle").getCount());
            trafficData.setTruckCount(vehicles.get("truck").getCount());
            
            trafficData.setPedestrianSpeed(vehicles.get("person").getAverageSpeed());
            trafficData.setCarSpeed(vehicles.get("car").getAverageSpeed());
            trafficData.setBicycleSpeed(vehicles.get("bicycle").getAverageSpeed());
            trafficData.setBusSpeed(vehicles.get("bus").getAverageSpeed());
            trafficData.setMotorcycleSpeed(vehicles.get("motorcycle").getAverageSpeed());
            trafficData.setTruckSpeed(vehicles.get("truck").getAverageSpeed());
            
            trafficData.setVolume(trafficInfo.getTotalVolume());
            trafficData.setCongestionLevel(trafficInfo.getCongestionLevel());
            
            trafficDataRepository.save(trafficData);
            log.debug("Saved traffic data: volume={}, congestion={}", 
                trafficInfo.getTotalVolume(), trafficInfo.getCongestionLevelText());
        } catch (Exception e) {
            log.error("Error saving traffic data", e);
        }
    }
    
    private void sendTrafficDataToKafka(TrafficInfoDto trafficInfo) {
        try {
            String message = objectMapper.writeValueAsString(trafficInfo);
            kafkaTemplate.send("traffic-data", message);
            log.debug("Sent traffic data to Kafka");
        } catch (Exception e) {
            log.error("Error sending traffic data to Kafka", e);
        }
    }
    
    private void sendAccidentNotificationToKafka(AccidentDetection accident) {
        try {
            String message = objectMapper.writeValueAsString(accident);
            kafkaTemplate.send("accident-notifications", message);
            log.info("Sent accident notification to Kafka: {}", accident.getId());
        } catch (Exception e) {
            log.error("Error sending accident notification to Kafka", e);
        }
    }
}

