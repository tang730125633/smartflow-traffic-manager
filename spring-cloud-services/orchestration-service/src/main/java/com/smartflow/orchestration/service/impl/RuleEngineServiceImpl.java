package com.smartflow.orchestration.service.impl;

import com.smartflow.orchestration.entity.SchedulingRule;
import com.smartflow.orchestration.repository.SchedulingRuleRepository;
import com.smartflow.orchestration.service.RuleEngineService;
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
 * 规则引擎服务实现
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {
    
    private final SchedulingRuleRepository ruleRepository;
    
    @Override
    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
    public SchedulingRule createRule(SchedulingRule rule) {
        log.info("创建调度规则: {}", rule.getRuleName());
        
        // 验证规则
        validateRule(rule);
        
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setExecutionCount(0L);
        
        return ruleRepository.save(rule);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
    public SchedulingRule updateRule(Long ruleId, SchedulingRule rule) {
        log.info("更新调度规则: {}", ruleId);
        
        SchedulingRule existingRule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("调度规则不存在: " + ruleId));
        
        // 更新规则属性
        existingRule.setRuleName(rule.getRuleName());
        existingRule.setDescription(rule.getDescription());
        existingRule.setRuleType(rule.getRuleType());
        existingRule.setPriority(rule.getPriority());
        existingRule.setConditions(rule.getConditions());
        existingRule.setActions(rule.getActions());
        existingRule.setSignalIds(rule.getSignalIds());
        existingRule.setTimeRanges(rule.getTimeRanges());
        existingRule.setEnabled(rule.getEnabled());
        existingRule.setAutoExecute(rule.getAutoExecute());
        existingRule.setUpdatedAt(LocalDateTime.now());
        
        // 验证规则
        validateRule(existingRule);
        
        return ruleRepository.save(existingRule);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
    public void deleteRule(Long ruleId) {
        log.info("删除调度规则: {}", ruleId);
        
        if (!ruleRepository.existsById(ruleId)) {
            throw new RuntimeException("调度规则不存在: " + ruleId);
        }
        
        ruleRepository.deleteById(ruleId);
    }
    
    @Override
    @Cacheable(value = "rules", key = "'all'")
    public List<SchedulingRule> getAllRules() {
        log.debug("获取所有调度规则");
        return ruleRepository.findAll();
    }
    
    @Override
    @Cacheable(value = "rules", key = "'enabled'")
    public List<SchedulingRule> getEnabledRules() {
        log.debug("获取启用的调度规则");
        return ruleRepository.findByEnabledTrueOrderByPriorityDesc();
    }
    
    @Override
    @Cacheable(value = "rules", key = "#ruleType")
    public List<SchedulingRule> getRulesByType(SchedulingRule.RuleType ruleType) {
        log.debug("根据类型获取调度规则: {}", ruleType);
        return ruleRepository.findByRuleTypeAndEnabledTrue(ruleType);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
    public void toggleRule(Long ruleId, Boolean enabled) {
        log.info("切换规则状态: {} -> {}", ruleId, enabled);
        
        SchedulingRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("调度规则不存在: " + ruleId));
        
        rule.setEnabled(enabled);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }
    
    @Override
    @Scheduled(fixedRate = 30000) // 每30秒执行一次
    @Transactional
    public void evaluateRules() {
        log.debug("执行规则评估");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(30);
        List<SchedulingRule> rulesToExecute = ruleRepository.findRulesToExecute(cutoffTime);
        
        for (SchedulingRule rule : rulesToExecute) {
            try {
                executeRule(rule);
            } catch (Exception e) {
                log.error("规则 {} 执行失败: {}", rule.getRuleName(), e.getMessage());
            }
        }
    }
    
    @Override
    @Transactional
    public void executeRule(Long ruleId) {
        log.info("执行调度规则: {}", ruleId);
        
        SchedulingRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("调度规则不存在: " + ruleId));
        
        if (!rule.getEnabled()) {
            throw new RuntimeException("调度规则已禁用: " + ruleId);
        }
        
        executeRule(rule);
    }
    
    @Override
    public boolean validateRuleConditions(String conditions) {
        try {
            // 这里可以添加更复杂的条件验证逻辑
            // 简化实现，检查JSON格式
            return conditions != null && !conditions.trim().isEmpty();
        } catch (Exception e) {
            log.error("规则条件验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean validateRuleActions(String actions) {
        try {
            // 这里可以添加更复杂的动作验证逻辑
            // 简化实现，检查JSON格式
            return actions != null && !actions.trim().isEmpty();
        } catch (Exception e) {
            log.error("规则动作验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getRuleStatistics() {
        log.debug("获取规则统计信息");
        
        List<Object[]> typeCounts = ruleRepository.countByRuleType();
        Map<String, Long> typeStats = typeCounts.stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
        
        long totalRules = ruleRepository.count();
        long enabledRules = ruleRepository.findByEnabledTrueOrderByPriorityDesc().size();
        long autoExecuteRules = ruleRepository.findByAutoExecuteTrueAndEnabledTrueOrderByPriorityDesc().size();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRules", totalRules);
        statistics.put("enabledRules", enabledRules);
        statistics.put("autoExecuteRules", autoExecuteRules);
        statistics.put("typeDistribution", typeStats);
        
        return statistics;
    }
    
    @Override
    @Transactional
    public void batchExecuteRules(List<Long> ruleIds) {
        log.info("批量执行规则: {} 个", ruleIds.size());
        
        for (Long ruleId : ruleIds) {
            try {
                executeRule(ruleId);
            } catch (Exception e) {
                log.error("规则 {} 执行失败: {}", ruleId, e.getMessage());
            }
        }
    }
    
    @Override
    public List<Map<String, Object>> getRuleExecutionHistory(Long ruleId) {
        log.debug("获取规则执行历史: {}", ruleId);
        
        SchedulingRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("调度规则不存在: " + ruleId));
        
        // 简化实现，返回基本信息
        List<Map<String, Object>> history = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("ruleId", rule.getId());
        record.put("ruleName", rule.getRuleName());
        record.put("executionCount", rule.getExecutionCount());
        record.put("lastExecutedAt", rule.getLastExecutedAt());
        history.add(record);
        
        return history;
    }
    
    /**
     * 执行规则
     */
    private void executeRule(SchedulingRule rule) {
        log.debug("执行规则: {}", rule.getRuleName());
        
        // 验证规则条件
        if (!evaluateConditions(rule.getConditions())) {
            log.debug("规则条件不满足: {}", rule.getRuleName());
            return;
        }
        
        // 执行规则动作
        executeActions(rule.getActions());
        
        // 更新执行统计
        rule.setExecutionCount(rule.getExecutionCount() + 1);
        rule.setLastExecutedAt(LocalDateTime.now());
        ruleRepository.save(rule);
        
        log.info("规则执行完成: {}", rule.getRuleName());
    }
    
    /**
     * 评估规则条件
     */
    private boolean evaluateConditions(String conditions) {
        // 这里实现具体的条件评估逻辑
        // 简化实现，返回true
        return true;
    }
    
    /**
     * 执行规则动作
     */
    private void executeActions(String actions) {
        // 这里实现具体的动作执行逻辑
        log.debug("执行规则动作: {}", actions);
    }
    
    /**
     * 验证规则
     */
    private void validateRule(SchedulingRule rule) {
        if (rule.getRuleName() == null || rule.getRuleName().trim().isEmpty()) {
            throw new IllegalArgumentException("规则名称不能为空");
        }
        
        if (rule.getRuleType() == null) {
            throw new IllegalArgumentException("规则类型不能为空");
        }
        
        if (rule.getPriority() == null || rule.getPriority() < 1) {
            throw new IllegalArgumentException("规则优先级必须大于0");
        }
        
        if (!validateRuleConditions(rule.getConditions())) {
            throw new IllegalArgumentException("规则条件格式不正确");
        }
        
        if (!validateRuleActions(rule.getActions())) {
            throw new IllegalArgumentException("规则动作格式不正确");
        }
    }
}
