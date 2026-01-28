package com.smartflow.orchestration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 调度规则实体
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Entity
@Table(name = "scheduling_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 规则名称
     */
    @Column(name = "rule_name", nullable = false)
    private String ruleName;
    
    /**
     * 规则描述
     */
    @Column(name = "description")
    private String description;
    
    /**
     * 规则类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;
    
    /**
     * 规则优先级
     */
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    /**
     * 规则条件(JSON格式)
     */
    @Column(name = "conditions", columnDefinition = "TEXT")
    private String conditions;
    
    /**
     * 规则动作(JSON格式)
     */
    @Column(name = "actions", columnDefinition = "TEXT")
    private String actions;
    
    /**
     * 适用信号灯ID列表(JSON格式)
     */
    @Column(name = "signal_ids", columnDefinition = "TEXT")
    private String signalIds;
    
    /**
     * 适用时间段(JSON格式)
     */
    @Column(name = "time_ranges", columnDefinition = "TEXT")
    private String timeRanges;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled;
    
    /**
     * 是否自动执行
     */
    @Column(name = "auto_execute")
    private Boolean autoExecute;
    
    /**
     * 执行次数
     */
    @Column(name = "execution_count")
    private Long executionCount;
    
    /**
     * 最后执行时间
     */
    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        executionCount = 0L;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 规则类型枚举
     */
    public enum RuleType {
        TRAFFIC_VOLUME("交通流量规则"),
        INCIDENT_RESPONSE("事件响应规则"),
        TIME_BASED("时间规则"),
        WEATHER_BASED("天气规则"),
        EMERGENCY("紧急规则"),
        CUSTOM("自定义规则");
        
        private final String description;
        
        RuleType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
