package com.smartflow.ai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 事故检测实体
 */
@Entity
@Table(name = "accident_detections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccidentDetection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "involved_vehicles", nullable = false)
    private String involvedVehicles;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "severity")
    private String severity = "Unknown";
    
    @Column(name = "status")
    private String status = "Pending";
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "description")
    private String description;
}

