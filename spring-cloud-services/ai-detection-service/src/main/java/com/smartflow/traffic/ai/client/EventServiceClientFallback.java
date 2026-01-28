package com.smartflow.traffic.ai.client;

import com.smartflow.traffic.ai.dto.EventDTO;
import com.smartflow.traffic.ai.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 事件管理服务Feign客户端降级处理
 * 当事件管理服务不可用时提供降级处理
 */
@Slf4j
@Component
public class EventServiceClientFallback implements EventServiceClient {

    @Override
    public ApiResponse<EventDTO> createEvent(EventDTO eventDTO) {
        log.error("事件管理服务不可用，无法创建事件: {}", eventDTO);
        return ApiResponse.error("事件管理服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<EventDTO> updateEventStatus(Long eventId, String status) {
        log.error("事件管理服务不可用，无法更新事件状态: eventId={}, status={}", eventId, status);
        return ApiResponse.error("事件管理服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<EventDTO> getEventById(Long eventId) {
        log.error("事件管理服务不可用，无法获取事件: eventId={}", eventId);
        return ApiResponse.error("事件管理服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<EventDTO> getEventByDetectionId(Long detectionId) {
        log.error("事件管理服务不可用，无法根据检测ID获取事件: detectionId={}", detectionId);
        return ApiResponse.error("事件管理服务暂时不可用，请稍后重试");
    }
}

