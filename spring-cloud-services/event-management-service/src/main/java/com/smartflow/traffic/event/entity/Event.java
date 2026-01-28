package com.smartflow.traffic.event.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交通事件实体类
 */
@Entity
@Table(name = "traffic_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件类型
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * 事件标题
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 事件描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 事件状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    /**
     * 事件级别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private EventLevel level;

    /**
     * 检测ID（关联AI检测服务）
     */
    @Column(name = "detection_id")
    private Long detectionId;

    /**
     * 摄像头ID
     */
    @Column(name = "camera_id")
    private Long cameraId;

    /**
     * 摄像头位置
     */
    @Column(name = "camera_location")
    private String cameraLocation;

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
     * 置信度
     */
    @Column(name = "confidence")
    private Double confidence;

    /**
     * 事件开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 事件结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 处理人员
     */
    @Column(name = "handler")
    private String handler;

    /**
     * 处理备注
     */
    @Column(name = "handler_notes", columnDefinition = "TEXT")
    private String handlerNotes;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
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
}

