package com.smartflow.ai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交通数据实体
 */
@Entity
@Table(name = "traffic_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    
    @Column(name = "pedestrian_speed")
    private Double pedestrianSpeed = 0.0;
    
    @Column(name = "car_speed")
    private Double carSpeed = 0.0;
    
    @Column(name = "bicycle_speed")
    private Double bicycleSpeed = 0.0;
    
    @Column(name = "bus_speed")
    private Double busSpeed = 0.0;
    
    @Column(name = "motorcycle_speed")
    private Double motorcycleSpeed = 0.0;
    
    @Column(name = "truck_speed")
    private Double truckSpeed = 0.0;
    
    @Column(name = "volume")
    private Double volume = 0.0;
    
    @Column(name = "congestion_level")
    private Integer congestionLevel = 0; // 0=Low, 1=Medium, 2=High
}

