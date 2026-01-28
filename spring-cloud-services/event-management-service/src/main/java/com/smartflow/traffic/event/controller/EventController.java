package com.smartflow.traffic.event.controller;

import com.smartflow.traffic.event.dto.ApiResponse;
import com.smartflow.traffic.event.dto.EventDTO;
import com.smartflow.traffic.event.entity.EventStatus;
import com.smartflow.traffic.event.entity.EventLevel;
import com.smartflow.traffic.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 创建事件
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        log.info("创建事件请求: {}", eventDTO.getTitle());
        
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("事件创建成功", createdEvent));
    }

    /**
     * 根据ID获取事件
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) {
        log.debug("获取事件请求: ID={}", id);
        
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    /**
     * 根据检测ID获取事件
     */
    @GetMapping("/by-detection/{detectionId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventByDetectionId(@PathVariable Long detectionId) {
        log.debug("根据检测ID获取事件请求: detectionId={}", detectionId);
        
        EventDTO event = eventService.getEventByDetectionId(detectionId);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    /**
     * 更新事件
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(@PathVariable Long id, 
                                                           @Valid @RequestBody EventDTO eventDTO) {
        log.info("更新事件请求: ID={}", id);
        
        EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(ApiResponse.success("事件更新成功", updatedEvent));
    }

    /**
     * 更新事件状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EventDTO>> updateEventStatus(@PathVariable Long id,
                                                                 @RequestParam EventStatus status,
                                                                 @RequestParam(required = false) String handler,
                                                                 @RequestParam(required = false) String notes) {
        log.info("更新事件状态请求: ID={}, 状态={}, 处理人={}", id, status, handler);
        
        EventDTO updatedEvent = eventService.updateEventStatus(id, status, handler, notes);
        return ResponseEntity.ok(ApiResponse.success("事件状态更新成功", updatedEvent));
    }

    /**
     * 删除事件
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        log.info("删除事件请求: ID={}", id);
        
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("事件删除成功", null));
    }

    /**
     * 分页查询事件
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventDTO>>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.debug("分页查询事件请求: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EventDTO> events = eventService.getEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 根据状态查询事件
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByStatus(@PathVariable EventStatus status) {
        log.debug("根据状态查询事件请求: status={}", status);
        
        List<EventDTO> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 根据级别查询事件
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByLevel(@PathVariable EventLevel level) {
        log.debug("根据级别查询事件请求: level={}", level);
        
        List<EventDTO> events = eventService.getEventsByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 根据摄像头ID查询事件
     */
    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByCameraId(@PathVariable Long cameraId) {
        log.debug("根据摄像头ID查询事件请求: cameraId={}", cameraId);
        
        List<EventDTO> events = eventService.getEventsByCameraId(cameraId);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 查询活跃事件
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getActiveEvents() {
        log.debug("查询活跃事件请求");
        
        List<EventDTO> events = eventService.getActiveEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 根据时间范围查询事件
     */
    @GetMapping("/time-range")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        log.debug("根据时间范围查询事件请求: {} - {}", startTime, endTime);
        
        List<EventDTO> events = eventService.getEventsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 获取事件统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<EventService.EventStatistics>> getEventStatistics() {
        log.debug("获取事件统计信息请求");
        
        EventService.EventStatistics statistics = eventService.getEventStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * 手动触发自动关闭超时事件
     */
    @PostMapping("/auto-close")
    public ResponseEntity<ApiResponse<Void>> triggerAutoClose() {
        log.info("手动触发自动关闭超时事件");
        
        eventService.autoCloseTimeoutEvents();
        return ResponseEntity.ok(ApiResponse.success("自动关闭超时事件完成", null));
    }
}

