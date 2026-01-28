package com.smartflow.ai.service;

import com.smartflow.ai.dto.TrafficDataDTO;
import com.smartflow.ai.entity.TrafficData;
import com.smartflow.ai.repository.TrafficDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 交通数据服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficDataService {
    
    private final TrafficDataRepository trafficDataRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private static final String REDIS_KEY_PREFIX = "traffic:data:";
    private static final String KAFKA_TOPIC = "traffic-data-topic";
    
    /**
     * 保存交通数据
     */
    @Transactional
    public TrafficDataDTO saveTrafficData(TrafficDataDTO trafficDataDTO) {
        log.info("保存交通数据: {}", trafficDataDTO);
        
        TrafficData trafficData = new TrafficData();
        BeanUtils.copyProperties(trafficDataDTO, trafficData);
        
        TrafficData savedData = trafficDataRepository.save(trafficData);
        
        // 缓存到Redis
        String redisKey = REDIS_KEY_PREFIX + savedData.getId();
        redisTemplate.opsForValue().set(redisKey, savedData, 1, TimeUnit.HOURS);
        
        // 发送到Kafka
        try {
            kafkaTemplate.send(KAFKA_TOPIC, "traffic-data", convertToJson(savedData));
            log.info("交通数据已发送到Kafka主题: {}", KAFKA_TOPIC);
        } catch (Exception e) {
            log.error("发送交通数据到Kafka失败", e);
        }
        
        return convertToDTO(savedData);
    }
    
    /**
     * 根据ID查询交通数据
     */
    public TrafficDataDTO getTrafficDataById(Long id) {
        // 先从Redis缓存中获取
        String redisKey = REDIS_KEY_PREFIX + id;
        TrafficData cachedData = (TrafficData) redisTemplate.opsForValue().get(redisKey);
        
        if (cachedData != null) {
            log.debug("从Redis缓存获取交通数据: {}", id);
            return convertToDTO(cachedData);
        }
        
        // 从数据库查询
        TrafficData trafficData = trafficDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("交通数据不存在: " + id));
        
        // 缓存到Redis
        redisTemplate.opsForValue().set(redisKey, trafficData, 1, TimeUnit.HOURS);
        
        return convertToDTO(trafficData);
    }
    
    /**
     * 根据时间范围查询交通数据
     */
    public List<TrafficDataDTO> getTrafficDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<TrafficData> trafficDataList = trafficDataRepository.findByDateTimeBetweenOrderByDateTimeDesc(startTime, endTime);
        return trafficDataList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最新的交通数据
     */
    public List<TrafficDataDTO> getLatestTrafficData() {
        List<TrafficData> trafficDataList = trafficDataRepository.findLatestTrafficData();
        return trafficDataList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据拥堵级别查询交通数据
     */
    public List<TrafficDataDTO> getTrafficDataByCongestionLevel(Integer congestionLevel) {
        List<TrafficData> trafficDataList = trafficDataRepository.findByCongestionLevelOrderByDateTimeDesc(congestionLevel);
        return trafficDataList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取平均交通量
     */
    public Double getAverageVolume(LocalDateTime startTime, LocalDateTime endTime) {
        return trafficDataRepository.getAverageVolumeByTimeRange(startTime, endTime);
    }
    
    /**
     * 获取平均车速
     */
    public Double getAverageCarSpeed(LocalDateTime startTime, LocalDateTime endTime) {
        return trafficDataRepository.getAverageCarSpeedByTimeRange(startTime, endTime);
    }
    
    /**
     * 删除交通数据
     */
    @Transactional
    public void deleteTrafficData(Long id) {
        trafficDataRepository.deleteById(id);
        
        // 从Redis缓存中删除
        String redisKey = REDIS_KEY_PREFIX + id;
        redisTemplate.delete(redisKey);
        
        log.info("删除交通数据: {}", id);
    }
    
    /**
     * 转换为DTO
     */
    private TrafficDataDTO convertToDTO(TrafficData trafficData) {
        TrafficDataDTO dto = new TrafficDataDTO();
        BeanUtils.copyProperties(trafficData, dto);
        return dto;
    }
    
    /**
     * 转换为JSON字符串
     */
    private String convertToJson(TrafficData trafficData) {
        // 这里可以使用Jackson或其他JSON库
        // 为了简化，这里返回一个简单的字符串
        return String.format("{\"id\":%d,\"dateTime\":\"%s\",\"volume\":%s}", 
                trafficData.getId(), 
                trafficData.getDateTime(), 
                trafficData.getVolume());
    }
}

