package com.smartflow.orchestration.service;

import com.smartflow.orchestration.entity.SchedulingRule;

import java.util.List;
import java.util.Map;

/**
 * 规则引擎服务接口
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
public interface RuleEngineService {
    
    /**
     * 创建调度规则
     */
    SchedulingRule createRule(SchedulingRule rule);
    
    /**
     * 更新调度规则
     */
    SchedulingRule updateRule(Long ruleId, SchedulingRule rule);
    
    /**
     * 删除调度规则
     */
    void deleteRule(Long ruleId);
    
    /**
     * 获取所有规则
     */
    List<SchedulingRule> getAllRules();
    
    /**
     * 获取启用的规则
     */
    List<SchedulingRule> getEnabledRules();
    
    /**
     * 根据类型获取规则
     */
    List<SchedulingRule> getRulesByType(SchedulingRule.RuleType ruleType);
    
    /**
     * 启用/禁用规则
     */
    void toggleRule(Long ruleId, Boolean enabled);
    
    /**
     * 执行规则评估
     */
    void evaluateRules();
    
    /**
     * 执行指定规则
     */
    void executeRule(Long ruleId);
    
    /**
     * 验证规则条件
     */
    boolean validateRuleConditions(String conditions);
    
    /**
     * 验证规则动作
     */
    boolean validateRuleActions(String actions);
    
    /**
     * 获取规则统计信息
     */
    Map<String, Object> getRuleStatistics();
    
    /**
     * 批量执行规则
     */
    void batchExecuteRules(List<Long> ruleIds);
    
    /**
     * 获取规则执行历史
     */
    List<Map<String, Object>> getRuleExecutionHistory(Long ruleId);
}
