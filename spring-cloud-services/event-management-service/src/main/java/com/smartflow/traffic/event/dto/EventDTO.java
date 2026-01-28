package com.smartflow.traffic.event.dto;

import com.smartflow.traffic.event.entity.EventLevel;
import com.smartflow.traffic.event.entity.EventStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 事件DTO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    private Long id;

    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    @NotBlank(message = "事件标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "事件状态不能为空")
    private EventStatus status;

    @NotNull(message = "事件级别不能为空")
    private EventLevel level;

    private Long detectionId;

    private Long cameraId;

    private String cameraLocation;

    private Double longitude;

    private Double latitude;

    private Double confidence;

    @NotNull(message = "事件开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String handler;

    private String handlerNotes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

