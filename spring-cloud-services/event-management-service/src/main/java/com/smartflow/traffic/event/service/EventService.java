package com.smartflow.traffic.event.service;

import com.smartflow.traffic.event.dto.EventDTO;
import com.smartflow.traffic.event.entity.EventStatus;
import com.smartflow.traffic.event.entity.EventLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件服务接口
 */
public interface EventService {

    /**
     * 创建事件
     */
    EventDTO createEvent(EventDTO eventDTO);

    /**
     * 根据ID获取事件
     */
    EventDTO getEventById(Long id);

    /**
     * 根据检测ID获取事件
     */
    EventDTO getEventByDetectionId(Long detectionId);

    /**
     * 更新事件
     */
    EventDTO updateEvent(Long id, EventDTO eventDTO);

    /**
     * 更新事件状态
     */
    EventDTO updateEventStatus(Long id, EventStatus status, String handler, String notes);

    /**
     * 删除事件
     */
    void deleteEvent(Long id);

    /**
     * 分页查询事件
     */
    Page<EventDTO> getEvents(Pageable pageable);

    /**
     * 根据状态查询事件
     */
    List<EventDTO> getEventsByStatus(EventStatus status);

    /**
     * 根据级别查询事件
     */
    List<EventDTO> getEventsByLevel(EventLevel level);

    /**
     * 根据摄像头ID查询事件
     */
    List<EventDTO> getEventsByCameraId(Long cameraId);

    /**
     * 查询活跃事件（未关闭）
     */
    List<EventDTO> getActiveEvents();

    /**
     * 查询指定时间范围内的事件
     */
    List<EventDTO> getEventsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 自动关闭超时事件
     */
    void autoCloseTimeoutEvents();

    /**
     * 获取事件统计信息
     */
    EventStatistics getEventStatistics();

    /**
     * 事件统计信息内部类
     */
    class EventStatistics {
        private long totalEvents;
        private long pendingEvents;
        private long inProgressEvents;
        private long resolvedEvents;
        private long closedEvents;
        private long falseAlarmEvents;

        // Getters and Setters
        public long getTotalEvents() { return totalEvents; }
        public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }

        public long getPendingEvents() { return pendingEvents; }
        public void setPendingEvents(long pendingEvents) { this.pendingEvents = pendingEvents; }

        public long getInProgressEvents() { return inProgressEvents; }
        public void setInProgressEvents(long inProgressEvents) { this.inProgressEvents = inProgressEvents; }

        public long getResolvedEvents() { return resolvedEvents; }
        public void setResolvedEvents(long resolvedEvents) { this.resolvedEvents = resolvedEvents; }

        public long getClosedEvents() { return closedEvents; }
        public void setClosedEvents(long closedEvents) { this.closedEvents = closedEvents; }

        public long getFalseAlarmEvents() { return falseAlarmEvents; }
        public void setFalseAlarmEvents(long falseAlarmEvents) { this.falseAlarmEvents = falseAlarmEvents; }
    }
}

