package com.smartflow.orchestration.service.impl;

import com.smartflow.orchestration.entity.TrafficSignal;
import com.smartflow.orchestration.entity.SchedulingRule;
import com.smartflow.orchestration.repository.TrafficSignalRepository;
import com.smartflow.orchestration.repository.SchedulingRuleRepository;
import com.smartflow.orchestration.service.SignalSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 信号调度服务实现
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignalSchedulingServiceImpl implements SignalSchedulingService {
    
    private final TrafficSignalRepository signalRepository;
    private final SchedulingRuleRepository ruleRepository;
    
    @Override
    @Cacheable(value = "signals", key = "'all'")
    public List<TrafficSignal> getAllSignals() {
        log.debug("获取所有信号灯状态");
        return signalRepository.findAll();
    }
    
    @Override
    @Cacheable(value = "signals", key = "#signalId")
    public TrafficSignal getSignalById(String signalId) {
        log.debug("获取信号灯: {}", signalId);
        return signalRepository.findBySignalId(signalId)
                .orElseThrow(() -> new RuntimeException("信号灯不存在: " + signalId));
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "signals", allEntries = true)
    public TrafficSignal updateSignalStatus(String signalId, TrafficSignal.SignalStatus status, Integer remainingTime) {
        log.info("更新信号灯状态: {} -> {}", signalId, status);
        
        TrafficSignal signal = getSignalById(signalId);
        signal.setCurrentStatus(status);
        signal.setRemainingTime(remainingTime);
        signal.setUpdatedAt(LocalDateTime.now());
        
        return signalRepository.save(signal);
    }
    
    @Override
    @Scheduled(fixedRate = 30000) // 每30秒执行一次
    @Transactional
    public void executeScheduling() {
        log.debug("执行自动信号调度");
        
        List<TrafficSignal> autoSignals = signalRepository.findByAutoSchedulingTrue();
        List<SchedulingRule> rules = ruleRepository.findByAutoExecuteTrueAndEnabledTrueOrderByPriorityDesc();
        
        for (TrafficSignal signal : autoSignals) {
            try {
                executeRulesForSignal(signal, rules);
            } catch (Exception e) {
                log.error("信号灯 {} 调度执行失败: {}", signal.getSignalId(), e.getMessage());
            }
        }
    }
    
    @Override
    @Transactional
    public void adjustSignalByTrafficVolume(String signalId, Integer trafficVolume) {
        log.info("根据交通流量调整信号灯: {} 流量: {}", signalId, trafficVolume);
        
        TrafficSignal signal = getSignalById(signalId);
        
        // 根据流量调整绿灯时间
        Integer adjustedGreenTime = calculateGreenTimeByTraffic(trafficVolume, signal.getGreenTime());
        
        if (!adjustedGreenTime.equals(signal.getGreenTime())) {
            signal.setGreenTime(adjustedGreenTime);
            signal.setCycleTime(adjustedGreenTime + signal.getYellowTime() + signal.getRedTime());
            signalRepository.save(signal);
            
            log.info("信号灯 {} 绿灯时间调整为: {} 秒", signalId, adjustedGreenTime);
        }
    }
    
    @Override
    @Transactional
    public void adjustSignalByIncident(String signalId, String incidentType, Integer severity) {
        log.info("根据事件调整信号灯: {} 事件类型: {} 严重程度: {}", signalId, incidentType, severity);
        
        TrafficSignal signal = getSignalById(signalId);
        
        // 根据事件类型和严重程度调整信号
        switch (incidentType.toUpperCase()) {
            case "ACCIDENT":
                handleAccidentIncident(signal, severity);
                break;
            case "CONGESTION":
                handleCongestionIncident(signal, severity);
                break;
            case "EMERGENCY":
                handleEmergencyIncident(signal);
                break;
            default:
                log.warn("未知事件类型: {}", incidentType);
        }
    }
    
    @Override
    @Transactional
    public void executeEmergencyScheduling(String signalId) {
        log.warn("执行紧急调度: {}", signalId);
        
        TrafficSignal signal = getSignalById(signalId);
        
        // 紧急情况下，优先保证主要方向通行
        signal.setCurrentStatus(TrafficSignal.SignalStatus.GREEN);
        signal.setRemainingTime(60); // 紧急绿灯60秒
        signal.setAutoScheduling(false); // 暂时关闭自动调度
        
        signalRepository.save(signal);
        
        log.info("信号灯 {} 已切换到紧急模式", signalId);
    }
    
    @Override
    public Map<String, Object> getSignalStatistics() {
        log.debug("获取信号灯统计信息");
        
        List<Object[]> statusCounts = signalRepository.countByStatus();
        Map<String, Long> statusStats = statusCounts.stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
        
        long totalSignals = signalRepository.count();
        long enabledSignals = signalRepository.findByEnabledTrue().size();
        long autoSchedulingSignals = signalRepository.findByAutoSchedulingTrue().size();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSignals", totalSignals);
        statistics.put("enabledSignals", enabledSignals);
        statistics.put("autoSchedulingSignals", autoSchedulingSignals);
        statistics.put("statusDistribution", statusStats);
        
        return statistics;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "signals", allEntries = true)
    public void batchUpdateSignals(List<TrafficSignal> signals) {
        log.info("批量更新信号灯: {} 个", signals.size());
        signalRepository.saveAll(signals);
    }
    
    @Override
    @Transactional
    public void toggleAutoScheduling(String signalId, Boolean enabled) {
        log.info("切换自动调度: {} -> {}", signalId, enabled);
        
        TrafficSignal signal = getSignalById(signalId);
        signal.setAutoScheduling(enabled);
        signalRepository.save(signal);
    }
    
    @Override
    public List<SchedulingRule> getSchedulingRules() {
        return ruleRepository.findByEnabledTrueOrderByPriorityDesc();
    }
    
    @Override
    @Transactional
    public void executeSchedulingRule(Long ruleId) {
        log.info("执行调度规则: {}", ruleId);
        
        SchedulingRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("调度规则不存在: " + ruleId));
        
        if (!rule.getEnabled()) {
            throw new RuntimeException("调度规则已禁用: " + ruleId);
        }
        
        // 执行规则逻辑
        executeRuleLogic(rule);
        
        // 更新执行统计
        rule.setExecutionCount(rule.getExecutionCount() + 1);
        rule.setLastExecutedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }
    
    /**
     * 为信号灯执行规则
     */
    private void executeRulesForSignal(TrafficSignal signal, List<SchedulingRule> rules) {
        for (SchedulingRule rule : rules) {
            if (isRuleApplicable(signal, rule)) {
                executeRuleLogic(rule);
            }
        }
    }
    
    /**
     * 检查规则是否适用于信号灯
     */
    private boolean isRuleApplicable(TrafficSignal signal, SchedulingRule rule) {
        // 这里可以根据规则的条件进行判断
        // 简化实现，返回true
        return true;
    }
    
    /**
     * 执行规则逻辑
     */
    private void executeRuleLogic(SchedulingRule rule) {
        // 这里实现具体的规则执行逻辑
        log.debug("执行规则: {}", rule.getRuleName());
    }
    
    /**
     * 根据交通流量计算绿灯时间
     */
    private Integer calculateGreenTimeByTraffic(Integer trafficVolume, Integer currentGreenTime) {
        // 简化的流量-绿灯时间计算逻辑
        if (trafficVolume > 100) {
            return Math.min(currentGreenTime + 10, 60); // 最大60秒
        } else if (trafficVolume < 20) {
            return Math.max(currentGreenTime - 5, 15); // 最小15秒
        }
        return currentGreenTime;
    }
    
    /**
     * 处理事故事件
     */
    private void handleAccidentIncident(TrafficSignal signal, Integer severity) {
        if (severity >= 3) {
            // 严重事故，延长绿灯时间
            signal.setGreenTime(Math.min(signal.getGreenTime() + 20, 60));
        } else {
            // 轻微事故，适当延长
            signal.setGreenTime(Math.min(signal.getGreenTime() + 10, 60));
        }
        signalRepository.save(signal);
    }
    
    /**
     * 处理拥堵事件
     */
    private void handleCongestionIncident(TrafficSignal signal, Integer severity) {
        // 拥堵时延长绿灯时间
        signal.setGreenTime(Math.min(signal.getGreenTime() + 15, 60));
        signalRepository.save(signal);
    }
    
    /**
     * 处理紧急事件
     */
    private void handleEmergencyIncident(TrafficSignal signal) {
        // 紧急情况，立即切换到绿灯
        signal.setCurrentStatus(TrafficSignal.SignalStatus.GREEN);
        signal.setRemainingTime(30);
        signalRepository.save(signal);
    }
}
