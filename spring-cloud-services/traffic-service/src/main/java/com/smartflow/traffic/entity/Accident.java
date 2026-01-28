package com.smartflow.traffic.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 事故实体类
 */
@Entity
@Table(name = "accidents")
public class Accident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
    
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
    
    @Column(name = "involved", nullable = false)
    private String involved;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity = Severity.UNKNOWN;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "confidence_score", precision = 3, scale = 2)
    private Double confidenceScore;
    
    // 构造函数
    public Accident() {
        this.dateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public String getInvolved() {
        return involved;
    }
    
    public void setInvolved(String involved) {
        this.involved = involved;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    /**
     * 事故严重程度枚举
     */
    public enum Severity {
        LOW("轻微"),
        MEDIUM("中等"),
        HIGH("严重"),
        CRITICAL("致命"),
        UNKNOWN("未知");
        
        private final String description;
        
        Severity(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 事故状态枚举
     */
    public enum Status {
        PENDING("待处理"),
        IN_PROGRESS("处理中"),
        RESOLVED("已解决"),
        CANCELLED("已取消");
        
        private final String description;
        
        Status(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}


