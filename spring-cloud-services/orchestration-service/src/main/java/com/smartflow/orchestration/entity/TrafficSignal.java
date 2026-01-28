package com.smartflow.orchestration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交通信号灯实体
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Entity
@Table(name = "traffic_signals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficSignal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 信号灯ID
     */
    @Column(name = "signal_id", unique = true, nullable = false)
    private String signalId;
    
    /**
     * 信号灯名称
     */
    @Column(name = "signal_name", nullable = false)
    private String signalName;
    
    /**
     * 路口ID
     */
    @Column(name = "intersection_id", nullable = false)
    private String intersectionId;
    
    /**
     * 路口名称
     */
    @Column(name = "intersection_name")
    private String intersectionName;
    
    /**
     * 经度
     */
    @Column(name = "longitude")
    private Double longitude;
    
    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Double latitude;
    
    /**
     * 当前状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private SignalStatus currentStatus;
    
    /**
     * 当前相位
     */
    @Column(name = "current_phase")
    private Integer currentPhase;
    
    /**
     * 剩余时间(秒)
     */
    @Column(name = "remaining_time")
    private Integer remainingTime;
    
    /**
     * 信号周期时间(秒)
     */
    @Column(name = "cycle_time")
    private Integer cycleTime;
    
    /**
     * 绿灯时间(秒)
     */
    @Column(name = "green_time")
    private Integer greenTime;
    
    /**
     * 黄灯时间(秒)
     */
    @Column(name = "yellow_time")
    private Integer yellowTime;
    
    /**
     * 红灯时间(秒)
     */
    @Column(name = "red_time")
    private Integer redTime;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled;
    
    /**
     * 是否自动调度
     */
    @Column(name = "auto_scheduling")
    private Boolean autoScheduling;
    
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
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 信号灯状态枚举
     */
    public enum SignalStatus {
        RED("红灯"),
        YELLOW("黄灯"),
        GREEN("绿灯"),
        FLASHING_RED("闪烁红灯"),
        FLASHING_YELLOW("闪烁黄灯"),
        OFF("关闭");
        
        private final String description;
        
        SignalStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
