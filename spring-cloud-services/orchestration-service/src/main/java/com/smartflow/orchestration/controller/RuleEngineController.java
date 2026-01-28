package com.smartflow.orchestration.controller;

import com.smartflow.orchestration.entity.SchedulingRule;
import com.smartflow.orchestration.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 规则引擎控制器
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleEngineController {
    
    private final RuleEngineService ruleEngineService;
    
    /**
     * 创建调度规则
     */
    @PostMapping
    public ResponseEntity<SchedulingRule> createRule(@RequestBody SchedulingRule rule) {
        log.info("创建调度规则: {}", rule.getRuleName());
        SchedulingRule createdRule = ruleEngineService.createRule(rule);
        return ResponseEntity.ok(createdRule);
    }
    
    /**
     * 更新调度规则
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<SchedulingRule> updateRule(
            @PathVariable Long ruleId,
            @RequestBody SchedulingRule rule) {
        
        log.info("更新调度规则: {}", ruleId);
        SchedulingRule updatedRule = ruleEngineService.updateRule(ruleId, rule);
        return ResponseEntity.ok(updatedRule);
    }
    
    /**
     * 删除调度规则
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<String> deleteRule(@PathVariable Long ruleId) {
        log.info("删除调度规则: {}", ruleId);
        ruleEngineService.deleteRule(ruleId);
        return ResponseEntity.ok("规则删除成功");
    }
    
    /**
     * 获取所有规则
     */
    @GetMapping
    public ResponseEntity<List<SchedulingRule>> getAllRules() {
        log.info("获取所有调度规则");
        List<SchedulingRule> rules = ruleEngineService.getAllRules();
        return ResponseEntity.ok(rules);
    }
    
    /**
     * 获取启用的规则
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<SchedulingRule>> getEnabledRules() {
        log.info("获取启用的调度规则");
        List<SchedulingRule> rules = ruleEngineService.getEnabledRules();
        return ResponseEntity.ok(rules);
    }
    
    /**
     * 根据类型获取规则
     */
    @GetMapping("/type/{ruleType}")
    public ResponseEntity<List<SchedulingRule>> getRulesByType(
            @PathVariable SchedulingRule.RuleType ruleType) {
        
        log.info("根据类型获取调度规则: {}", ruleType);
        List<SchedulingRule> rules = ruleEngineService.getRulesByType(ruleType);
        return ResponseEntity.ok(rules);
    }
    
    /**
     * 启用/禁用规则
     */
    @PutMapping("/{ruleId}/toggle")
    public ResponseEntity<String> toggleRule(
            @PathVariable Long ruleId,
            @RequestParam Boolean enabled) {
        
        log.info("切换规则状态: {} -> {}", ruleId, enabled);
        ruleEngineService.toggleRule(ruleId, enabled);
        return ResponseEntity.ok("规则状态已更新");
    }
    
    /**
     * 执行规则评估
     */
    @PostMapping("/evaluate")
    public ResponseEntity<String> evaluateRules() {
        log.info("执行规则评估");
        ruleEngineService.evaluateRules();
        return ResponseEntity.ok("规则评估执行完成");
    }
    
    /**
     * 执行指定规则
     */
    @PostMapping("/{ruleId}/execute")
    public ResponseEntity<String> executeRule(@PathVariable Long ruleId) {
        log.info("执行调度规则: {}", ruleId);
        ruleEngineService.executeRule(ruleId);
        return ResponseEntity.ok("规则执行完成");
    }
    
    /**
     * 验证规则条件
     */
    @PostMapping("/validate/conditions")
    public ResponseEntity<Boolean> validateRuleConditions(@RequestParam String conditions) {
        log.info("验证规则条件");
        boolean isValid = ruleEngineService.validateRuleConditions(conditions);
        return ResponseEntity.ok(isValid);
    }
    
    /**
     * 验证规则动作
     */
    @PostMapping("/validate/actions")
    public ResponseEntity<Boolean> validateRuleActions(@RequestParam String actions) {
        log.info("验证规则动作");
        boolean isValid = ruleEngineService.validateRuleActions(actions);
        return ResponseEntity.ok(isValid);
    }
    
    /**
     * 获取规则统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRuleStatistics() {
        log.info("获取规则统计信息");
        Map<String, Object> statistics = ruleEngineService.getRuleStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 批量执行规则
     */
    @PostMapping("/batch-execute")
    public ResponseEntity<String> batchExecuteRules(@RequestBody List<Long> ruleIds) {
        log.info("批量执行规则: {} 个", ruleIds.size());
        ruleEngineService.batchExecuteRules(ruleIds);
        return ResponseEntity.ok("批量执行完成");
    }
    
    /**
     * 获取规则执行历史
     */
    @GetMapping("/{ruleId}/history")
    public ResponseEntity<List<Map<String, Object>>> getRuleExecutionHistory(@PathVariable Long ruleId) {
        log.info("获取规则执行历史: {}", ruleId);
        List<Map<String, Object>> history = ruleEngineService.getRuleExecutionHistory(ruleId);
        return ResponseEntity.ok(history);
    }
}
