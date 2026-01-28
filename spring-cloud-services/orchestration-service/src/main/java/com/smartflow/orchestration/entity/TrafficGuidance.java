package com.smartflow.orchestration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交通诱导实体
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Entity
@Table(name = "traffic_guidance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficGuidance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 诱导信息ID
     */
    @Column(name = "guidance_id", unique = true, nullable = false)
    private String guidanceId;
    
    /**
     * 诱导类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "guidance_type", nullable = false)
    private GuidanceType guidanceType;
    
    /**
     * 诱导标题
     */
    @Column(name = "title", nullable = false)
    private String title;
    
    /**
     * 诱导内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 目标区域
     */
    @Column(name = "target_area")
    private String targetArea;
    
    /**
     * 起始位置
     */
    @Column(name = "start_location")
    private String startLocation;
    
    /**
     * 目标位置
     */
    @Column(name = "end_location")
    private String endLocation;
    
    /**
     * 优先级
     */
    @Column(name = "priority")
    private Integer priority;
    
    /**
     * 有效期开始时间
     */
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    /**
     * 有效期结束时间
     */
    @Column(name = "valid_to")
    private LocalDateTime validTo;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled;
    
    /**
     * 显示次数
     */
    @Column(name = "display_count")
    private Long displayCount;
    
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
        displayCount = 0L;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 诱导类型枚举
     */
    public enum GuidanceType {
        ROUTE_GUIDANCE("路线诱导"),
        CONGESTION_WARNING("拥堵预警"),
        INCIDENT_NOTIFICATION("事件通知"),
        SPEED_LIMIT("限速提醒"),
        CONSTRUCTION_WARNING("施工提醒"),
        WEATHER_WARNING("天气预警"),
        EMERGENCY_ALERT("紧急警报");
        
        private final String description;
        
        GuidanceType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
