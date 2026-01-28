package com.smartflow.orchestration.service.impl;

import com.smartflow.orchestration.entity.TrafficGuidance;
import com.smartflow.orchestration.repository.TrafficGuidanceRepository;
import com.smartflow.orchestration.service.TrafficGuidanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交通诱导服务实现
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficGuidanceServiceImpl implements TrafficGuidanceService {
    
    private final TrafficGuidanceRepository guidanceRepository;
    
    @Override
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public TrafficGuidance createGuidance(TrafficGuidance guidance) {
        log.info("创建交通诱导信息: {}", guidance.getTitle());
        
        // 生成诱导ID
        if (guidance.getGuidanceId() == null) {
            guidance.setGuidanceId("GUIDANCE_" + System.currentTimeMillis());
        }
        
        guidance.setCreatedAt(LocalDateTime.now());
        guidance.setUpdatedAt(LocalDateTime.now());
        guidance.setDisplayCount(0L);
        
        return guidanceRepository.save(guidance);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public TrafficGuidance updateGuidance(Long guidanceId, TrafficGuidance guidance) {
        log.info("更新交通诱导信息: {}", guidanceId);
        
        TrafficGuidance existingGuidance = guidanceRepository.findById(guidanceId)
                .orElseThrow(() -> new RuntimeException("诱导信息不存在: " + guidanceId));
        
        // 更新诱导信息属性
        existingGuidance.setTitle(guidance.getTitle());
        existingGuidance.setContent(guidance.getContent());
        existingGuidance.setGuidanceType(guidance.getGuidanceType());
        existingGuidance.setTargetArea(guidance.getTargetArea());
        existingGuidance.setStartLocation(guidance.getStartLocation());
        existingGuidance.setEndLocation(guidance.getEndLocation());
        existingGuidance.setPriority(guidance.getPriority());
        existingGuidance.setValidFrom(guidance.getValidFrom());
        existingGuidance.setValidTo(guidance.getValidTo());
        existingGuidance.setEnabled(guidance.getEnabled());
        existingGuidance.setUpdatedAt(LocalDateTime.now());
        
        return guidanceRepository.save(existingGuidance);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public void deleteGuidance(Long guidanceId) {
        log.info("删除交通诱导信息: {}", guidanceId);
        
        if (!guidanceRepository.existsById(guidanceId)) {
            throw new RuntimeException("诱导信息不存在: " + guidanceId);
        }
        
        guidanceRepository.deleteById(guidanceId);
    }
    
    @Override
    @Cacheable(value = "guidance", key = "'all'")
    public List<TrafficGuidance> getAllGuidance() {
        log.debug("获取所有交通诱导信息");
        return guidanceRepository.findAll();
    }
    
    @Override
    @Cacheable(value = "guidance", key = "'valid'")
    public List<TrafficGuidance> getValidGuidance() {
        log.debug("获取当前有效的诱导信息");
        return guidanceRepository.findValidGuidance(LocalDateTime.now());
    }
    
    @Override
    @Cacheable(value = "guidance", key = "#type")
    public List<TrafficGuidance> getGuidanceByType(TrafficGuidance.GuidanceType type) {
        log.debug("根据类型获取诱导信息: {}", type);
        return guidanceRepository.findValidGuidanceByType(type, LocalDateTime.now());
    }
    
    @Override
    @Cacheable(value = "guidance", key = "#area")
    public List<TrafficGuidance> getGuidanceByArea(String area) {
        log.debug("根据区域获取诱导信息: {}", area);
        return guidanceRepository.findByTargetArea(area, LocalDateTime.now());
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public void toggleGuidance(Long guidanceId, Boolean enabled) {
        log.info("切换诱导信息状态: {} -> {}", guidanceId, enabled);
        
        TrafficGuidance guidance = guidanceRepository.findById(guidanceId)
                .orElseThrow(() -> new RuntimeException("诱导信息不存在: " + guidanceId));
        
        guidance.setEnabled(enabled);
        guidance.setUpdatedAt(LocalDateTime.now());
        guidanceRepository.save(guidance);
    }
    
    @Override
    public Map<String, Object> getGuidanceStatistics() {
        log.debug("获取诱导信息统计");
        
        List<Object[]> typeCounts = guidanceRepository.countByGuidanceType();
        Map<String, Long> typeStats = typeCounts.stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
        
        long totalGuidance = guidanceRepository.count();
        long validGuidance = guidanceRepository.findValidGuidance(LocalDateTime.now()).size();
        long enabledGuidance = guidanceRepository.findAll().stream()
                .filter(TrafficGuidance::getEnabled)
                .count();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalGuidance", totalGuidance);
        statistics.put("validGuidance", validGuidance);
        statistics.put("enabledGuidance", enabledGuidance);
        statistics.put("typeDistribution", typeStats);
        
        return statistics;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public void batchCreateGuidance(List<TrafficGuidance> guidanceList) {
        log.info("批量创建诱导信息: {} 个", guidanceList.size());
        
        for (TrafficGuidance guidance : guidanceList) {
            if (guidance.getGuidanceId() == null) {
                guidance.setGuidanceId("GUIDANCE_" + System.currentTimeMillis() + "_" + Math.random());
            }
            guidance.setCreatedAt(LocalDateTime.now());
            guidance.setUpdatedAt(LocalDateTime.now());
            guidance.setDisplayCount(0L);
        }
        
        guidanceRepository.saveAll(guidanceList);
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    @CacheEvict(value = "guidance", allEntries = true)
    public void cleanExpiredGuidance() {
        log.debug("清理过期诱导信息");
        
        LocalDateTime now = LocalDateTime.now();
        List<TrafficGuidance> expiredGuidance = guidanceRepository.findAll().stream()
                .filter(guidance -> guidance.getValidTo() != null && guidance.getValidTo().isBefore(now))
                .collect(Collectors.toList());
        
        if (!expiredGuidance.isEmpty()) {
            guidanceRepository.deleteAll(expiredGuidance);
            log.info("清理了 {} 条过期诱导信息", expiredGuidance.size());
        }
    }
    
    @Override
    @Cacheable(value = "guidance", key = "#priority")
    public List<TrafficGuidance> getGuidanceByPriority(Integer priority) {
        log.debug("根据优先级获取诱导信息: {}", priority);
        return guidanceRepository.findByPriorityAndEnabledTrueOrderByCreatedAtDesc(priority);
    }
    
    @Override
    @Transactional
    public void updateDisplayCount(Long guidanceId) {
        log.debug("更新诱导信息显示次数: {}", guidanceId);
        
        TrafficGuidance guidance = guidanceRepository.findById(guidanceId)
                .orElseThrow(() -> new RuntimeException("诱导信息不存在: " + guidanceId));
        
        guidance.setDisplayCount(guidance.getDisplayCount() + 1);
        guidance.setUpdatedAt(LocalDateTime.now());
        guidanceRepository.save(guidance);
    }
}
