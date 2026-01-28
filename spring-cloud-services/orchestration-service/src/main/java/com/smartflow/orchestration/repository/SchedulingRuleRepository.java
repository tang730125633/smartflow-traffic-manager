package com.smartflow.orchestration.repository;

import com.smartflow.orchestration.entity.SchedulingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度规则数据访问层
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Repository
public interface SchedulingRuleRepository extends JpaRepository<SchedulingRule, Long> {
    
    /**
     * 查找启用的规则
     */
    List<SchedulingRule> findByEnabledTrueOrderByPriorityDesc();
    
    /**
     * 根据规则类型查找
     */
    List<SchedulingRule> findByRuleTypeAndEnabledTrue(SchedulingRule.RuleType ruleType);
    
    /**
     * 查找自动执行的规则
     */
    List<SchedulingRule> findByAutoExecuteTrueAndEnabledTrueOrderByPriorityDesc();
    
    /**
     * 根据优先级范围查找
     */
    List<SchedulingRule> findByPriorityBetweenAndEnabledTrue(Integer minPriority, Integer maxPriority);
    
    /**
     * 查找需要执行的规则(基于时间)
     */
    @Query("SELECT sr FROM SchedulingRule sr WHERE " +
           "sr.enabled = true AND sr.autoExecute = true AND " +
           "(sr.lastExecutedAt IS NULL OR sr.lastExecutedAt < :cutoffTime)")
    List<SchedulingRule> findRulesToExecute(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 统计各类型规则数量
     */
    @Query("SELECT sr.ruleType, COUNT(sr) FROM SchedulingRule sr WHERE sr.enabled = true GROUP BY sr.ruleType")
    List<Object[]> countByRuleType();
}
