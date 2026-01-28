package com.smartflow.traffic.event.service;

import com.smartflow.traffic.event.dto.EventDTO;
import com.smartflow.traffic.event.entity.Event;
import org.springframework.stereotype.Component;

/**
 * 事件映射器
 * 负责Event实体和EventDTO之间的转换
 */
@Component
public class EventMapper {

    /**
     * 实体转DTO
     */
    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        return EventDTO.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .title(event.getTitle())
                .description(event.getDescription())
                .status(event.getStatus())
                .level(event.getLevel())
                .detectionId(event.getDetectionId())
                .cameraId(event.getCameraId())
                .cameraLocation(event.getCameraLocation())
                .longitude(event.getLongitude())
                .latitude(event.getLatitude())
                .confidence(event.getConfidence())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .handler(event.getHandler())
                .handlerNotes(event.getHandlerNotes())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    /**
     * DTO转实体
     */
    public Event toEntity(EventDTO eventDTO) {
        if (eventDTO == null) {
            return null;
        }

        return Event.builder()
                .id(eventDTO.getId())
                .eventType(eventDTO.getEventType())
                .title(eventDTO.getTitle())
                .description(eventDTO.getDescription())
                .status(eventDTO.getStatus())
                .level(eventDTO.getLevel())
                .detectionId(eventDTO.getDetectionId())
                .cameraId(eventDTO.getCameraId())
                .cameraLocation(eventDTO.getCameraLocation())
                .longitude(eventDTO.getLongitude())
                .latitude(eventDTO.getLatitude())
                .confidence(eventDTO.getConfidence())
                .startTime(eventDTO.getStartTime())
                .endTime(eventDTO.getEndTime())
                .handler(eventDTO.getHandler())
                .handlerNotes(eventDTO.getHandlerNotes())
                .createdAt(eventDTO.getCreatedAt())
                .updatedAt(eventDTO.getUpdatedAt())
                .build();
    }
}

