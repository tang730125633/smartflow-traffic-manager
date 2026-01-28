package com.smartflow.ai.service;

import com.smartflow.ai.dto.AccidentDetectionDTO;
import com.smartflow.ai.entity.AccidentDetection;
import com.smartflow.ai.repository.AccidentDetectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 事故检测服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccidentDetectionService {
    
    private final AccidentDetectionRepository accidentDetectionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private static final String REDIS_KEY_PREFIX = "accident:detection:";
    private static final String KAFKA_TOPIC = "accident-detection-topic";
    
    /**
     * 保存事故检测数据
     */
    @Transactional
    public AccidentDetectionDTO saveAccidentDetection(AccidentDetectionDTO accidentDetectionDTO) {
        log.info("保存事故检测数据: {}", accidentDetectionDTO);
        
        AccidentDetection accidentDetection = new AccidentDetection();
        BeanUtils.copyProperties(accidentDetectionDTO, accidentDetection);
        
        // 处理图片数据
        if (accidentDetectionDTO.getImageData() != null) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(accidentDetectionDTO.getImageData());
                accidentDetection.setImageData(imageBytes);
            } catch (Exception e) {
                log.error("图片数据解码失败", e);
                throw new RuntimeException("图片数据格式错误");
            }
        }
        
        // 设置严重程度和状态
        if (accidentDetectionDTO.getSeverity() != null) {
            accidentDetection.setSeverity(AccidentDetection.Severity.valueOf(accidentDetectionDTO.getSeverity()));
        }
        if (accidentDetectionDTO.getStatus() != null) {
            accidentDetection.setStatus(AccidentDetection.Status.valueOf(accidentDetectionDTO.getStatus()));
        }
        
        AccidentDetection savedData = accidentDetectionRepository.save(accidentDetection);
        
        // 缓存到Redis
        String redisKey = REDIS_KEY_PREFIX + savedData.getId();
        redisTemplate.opsForValue().set(redisKey, savedData, 2, TimeUnit.HOURS);
        
        // 发送到Kafka
        try {
            kafkaTemplate.send(KAFKA_TOPIC, "accident-detection", convertToJson(savedData));
            log.info("事故检测数据已发送到Kafka主题: {}", KAFKA_TOPIC);
        } catch (Exception e) {
            log.error("发送事故检测数据到Kafka失败", e);
        }
        
        return convertToDTO(savedData);
    }
    
    /**
     * 根据ID查询事故检测数据
     */
    public AccidentDetectionDTO getAccidentDetectionById(Long id) {
        // 先从Redis缓存中获取
        String redisKey = REDIS_KEY_PREFIX + id;
        AccidentDetection cachedData = (AccidentDetection) redisTemplate.opsForValue().get(redisKey);
        
        if (cachedData != null) {
            log.debug("从Redis缓存获取事故检测数据: {}", id);
            return convertToDTO(cachedData);
        }
        
        // 从数据库查询
        AccidentDetection accidentDetection = accidentDetectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("事故检测数据不存在: " + id));
        
        // 缓存到Redis
        redisTemplate.opsForValue().set(redisKey, accidentDetection, 2, TimeUnit.HOURS);
        
        return convertToDTO(accidentDetection);
    }
    
    /**
     * 根据时间范围查询事故检测数据
     */
    public List<AccidentDetectionDTO> getAccidentDetectionByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<AccidentDetection> accidentDetectionList = accidentDetectionRepository.findByDateTimeBetweenOrderByDateTimeDesc(startTime, endTime);
        return accidentDetectionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据状态查询事故检测数据
     */
    public List<AccidentDetectionDTO> getAccidentDetectionByStatus(String status) {
        AccidentDetection.Status statusEnum = AccidentDetection.Status.valueOf(status);
        List<AccidentDetection> accidentDetectionList = accidentDetectionRepository.findByStatusOrderByDateTimeDesc(statusEnum);
        return accidentDetectionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据严重程度查询事故检测数据
     */
    public List<AccidentDetectionDTO> getAccidentDetectionBySeverity(String severity) {
        AccidentDetection.Severity severityEnum = AccidentDetection.Severity.valueOf(severity);
        List<AccidentDetection> accidentDetectionList = accidentDetectionRepository.findBySeverityOrderByDateTimeDesc(severityEnum);
        return accidentDetectionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询高置信度的事故检测数据
     */
    public List<AccidentDetectionDTO> getHighConfidenceAccidents(Double threshold) {
        List<AccidentDetection> accidentDetectionList = accidentDetectionRepository.findHighConfidenceAccidents(threshold);
        return accidentDetectionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询待处理的事故检测数据
     */
    public List<AccidentDetectionDTO> getPendingAccidents() {
        List<AccidentDetection> accidentDetectionList = accidentDetectionRepository.findPendingAccidents();
        return accidentDetectionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 统计指定时间范围内的事故数量
     */
    public Long countAccidentsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return accidentDetectionRepository.countAccidentsByTimeRange(startTime, endTime);
    }
    
    /**
     * 更新事故检测状态
     */
    @Transactional
    public AccidentDetectionDTO updateAccidentStatus(Long id, String status) {
        AccidentDetection accidentDetection = accidentDetectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("事故检测数据不存在: " + id));
        
        accidentDetection.setStatus(AccidentDetection.Status.valueOf(status));
        AccidentDetection updatedData = accidentDetectionRepository.save(accidentDetection);
        
        // 更新Redis缓存
        String redisKey = REDIS_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(redisKey, updatedData, 2, TimeUnit.HOURS);
        
        return convertToDTO(updatedData);
    }
    
    /**
     * 删除事故检测数据
     */
    @Transactional
    public void deleteAccidentDetection(Long id) {
        accidentDetectionRepository.deleteById(id);
        
        // 从Redis缓存中删除
        String redisKey = REDIS_KEY_PREFIX + id;
        redisTemplate.delete(redisKey);
        
        log.info("删除事故检测数据: {}", id);
    }
    
    /**
     * 转换为DTO
     */
    private AccidentDetectionDTO convertToDTO(AccidentDetection accidentDetection) {
        AccidentDetectionDTO dto = new AccidentDetectionDTO();
        BeanUtils.copyProperties(accidentDetection, dto);
        
        // 处理图片数据
        if (accidentDetection.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(accidentDetection.getImageData());
            dto.setImageData(base64Image);
        }
        
        // 设置枚举值
        if (accidentDetection.getSeverity() != null) {
            dto.setSeverity(accidentDetection.getSeverity().name());
        }
        if (accidentDetection.getStatus() != null) {
            dto.setStatus(accidentDetection.getStatus().name());
        }
        
        return dto;
    }
    
    /**
     * 转换为JSON字符串
     */
    private String convertToJson(AccidentDetection accidentDetection) {
        // 这里可以使用Jackson或其他JSON库
        // 为了简化，这里返回一个简单的字符串
        return String.format("{\"id\":%d,\"dateTime\":\"%s\",\"confidenceScore\":%s,\"severity\":\"%s\"}", 
                accidentDetection.getId(), 
                accidentDetection.getDateTime(), 
                accidentDetection.getConfidenceScore(),
                accidentDetection.getSeverity());
    }
}

