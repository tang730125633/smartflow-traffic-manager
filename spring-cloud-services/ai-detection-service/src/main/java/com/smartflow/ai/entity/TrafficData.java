package com.smartflow.ai.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交通数据实体类
 */
@Entity
@Table(name = "traffic_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
    
    @Column(name = "pedestrian_count")
    private Integer pedestrianCount = 0;
    
    @Column(name = "car_count")
    private Integer carCount = 0;
    
    @Column(name = "bicycle_count")
    private Integer bicycleCount = 0;
    
    @Column(name = "bus_count")
    private Integer busCount = 0;
    
    @Column(name = "motorcycle_count")
    private Integer motorcycleCount = 0;
    
    @Column(name = "truck_count")
    private Integer truckCount = 0;
    
    @Column(name = "pedestrian_speed", precision = 5, scale = 2)
    private BigDecimal pedestrianSpeed = BigDecimal.ZERO;
    
    @Column(name = "car_speed", precision = 5, scale = 2)
    private BigDecimal carSpeed = BigDecimal.ZERO;
    
    @Column(name = "bicycle_speed", precision = 5, scale = 2)
    private BigDecimal bicycleSpeed = BigDecimal.ZERO;
    
    @Column(name = "bus_speed", precision = 5, scale = 2)
    private BigDecimal busSpeed = BigDecimal.ZERO;
    
    @Column(name = "motorcycle_speed", precision = 5, scale = 2)
    private BigDecimal motorcycleSpeed = BigDecimal.ZERO;
    
    @Column(name = "truck_speed", precision = 5, scale = 2)
    private BigDecimal truckSpeed = BigDecimal.ZERO;
    
    @Column(name = "volume", precision = 10, scale = 2)
    private BigDecimal volume = BigDecimal.ZERO;
    
    @Column(name = "congestion_level")
    private Integer congestionLevel = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
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
}

