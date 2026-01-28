package com.smartflow.traffic.ai.client;

import com.smartflow.traffic.ai.dto.EventDTO;
import com.smartflow.traffic.ai.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 事件管理服务Feign客户端
 * 用于调用事件管理服务的API
 */
@FeignClient(name = "event-management-service", fallback = EventServiceClientFallback.class)
public interface EventServiceClient {

    /**
     * 创建事件
     * @param eventDTO 事件DTO
     * @return 创建结果
     */
    @PostMapping("/api/events")
    ApiResponse<EventDTO> createEvent(@RequestBody EventDTO eventDTO);

    /**
     * 更新事件状态
     * @param eventId 事件ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/api/events/{eventId}/status")
    ApiResponse<EventDTO> updateEventStatus(@PathVariable("eventId") Long eventId, 
                                          @RequestParam("status") String status);

    /**
     * 根据ID获取事件
     * @param eventId 事件ID
     * @return 事件信息
     */
    @GetMapping("/api/events/{eventId}")
    ApiResponse<EventDTO> getEventById(@PathVariable("eventId") Long eventId);

    /**
     * 根据检测ID获取事件
     * @param detectionId 检测ID
     * @return 事件信息
     */
    @GetMapping("/api/events/by-detection/{detectionId}")
    ApiResponse<EventDTO> getEventByDetectionId(@PathVariable("detectionId") Long detectionId);
}

