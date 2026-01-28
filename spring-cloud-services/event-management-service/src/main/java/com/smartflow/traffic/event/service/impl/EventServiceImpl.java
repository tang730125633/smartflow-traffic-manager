package com.smartflow.traffic.event.service.impl;

import com.smartflow.traffic.event.dto.EventDTO;
import com.smartflow.traffic.event.entity.Event;
import com.smartflow.traffic.event.entity.EventStatus;
import com.smartflow.traffic.event.entity.EventLevel;
import com.smartflow.traffic.event.repository.EventRepository;
import com.smartflow.traffic.event.service.EventService;
import com.smartflow.traffic.event.exception.EventNotFoundException;
import com.smartflow.traffic.event.exception.InvalidStatusTransitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventDTO createEvent(EventDTO eventDTO) {
        log.info("创建事件: {}", eventDTO.getTitle());
        
        Event event = eventMapper.toEntity(eventDTO);
        event.setStatus(EventStatus.PENDING_CONFIRMATION);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        Event savedEvent = eventRepository.save(event);
        log.info("事件创建成功，ID: {}", savedEvent.getId());
        
        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @Cacheable(value = "events", key = "#id")
    public EventDTO getEventById(Long id) {
        log.debug("根据ID获取事件: {}", id);
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("事件不存在，ID: " + id));
        
        return eventMapper.toDTO(event);
    }

    @Override
    @Cacheable(value = "events", key = "'detection_' + #detectionId")
    public EventDTO getEventByDetectionId(Long detectionId) {
        log.debug("根据检测ID获取事件: {}", detectionId);
        
        Event event = eventRepository.findByDetectionId(detectionId)
                .orElseThrow(() -> new EventNotFoundException("事件不存在，检测ID: " + detectionId));
        
        return eventMapper.toDTO(event);
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        log.info("更新事件: {}", id);
        
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("事件不存在，ID: " + id));
        
        // 更新字段
        existingEvent.setEventType(eventDTO.getEventType());
        existingEvent.setTitle(eventDTO.getTitle());
        existingEvent.setDescription(eventDTO.getDescription());
        existingEvent.setLevel(eventDTO.getLevel());
        existingEvent.setCameraId(eventDTO.getCameraId());
        existingEvent.setCameraLocation(eventDTO.getCameraLocation());
        existingEvent.setLongitude(eventDTO.getLongitude());
        existingEvent.setLatitude(eventDTO.getLatitude());
        existingEvent.setConfidence(eventDTO.getConfidence());
        existingEvent.setHandler(eventDTO.getHandler());
        existingEvent.setHandlerNotes(eventDTO.getHandlerNotes());
        existingEvent.setUpdatedAt(LocalDateTime.now());
        
        Event savedEvent = eventRepository.save(existingEvent);
        log.info("事件更新成功，ID: {}", savedEvent.getId());
        
        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventDTO updateEventStatus(Long id, EventStatus status, String handler, String notes) {
        log.info("更新事件状态: ID={}, 状态={}, 处理人={}", id, status, handler);
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("事件不存在，ID: " + id));
        
        // 检查状态转换是否有效
        if (!event.getStatus().canTransitionTo(status)) {
            throw new InvalidStatusTransitionException(
                String.format("无效的状态转换: %s -> %s", event.getStatus(), status));
        }
        
        // 更新状态
        event.setStatus(status);
        event.setHandler(handler);
        event.setHandlerNotes(notes);
        event.setUpdatedAt(LocalDateTime.now());
        
        // 如果状态为已解决或已关闭，设置结束时间
        if (status == EventStatus.RESOLVED || status == EventStatus.CLOSED) {
            event.setEndTime(LocalDateTime.now());
        }
        
        Event savedEvent = eventRepository.save(event);
        log.info("事件状态更新成功，ID: {}, 新状态: {}", savedEvent.getId(), savedEvent.getStatus());
        
        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(Long id) {
        log.info("删除事件: {}", id);
        
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("事件不存在，ID: " + id);
        }
        
        eventRepository.deleteById(id);
        log.info("事件删除成功，ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEvents(Pageable pageable) {
        log.debug("分页查询事件: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Event> events = eventRepository.findAll(pageable);
        return events.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'status_' + #status")
    public List<EventDTO> getEventsByStatus(EventStatus status) {
        log.debug("根据状态查询事件: {}", status);
        
        List<Event> events = eventRepository.findByStatus(status);
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'level_' + #level")
    public List<EventDTO> getEventsByLevel(EventLevel level) {
        log.debug("根据级别查询事件: {}", level);
        
        List<Event> events = eventRepository.findByLevel(level);
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'camera_' + #cameraId")
    public List<EventDTO> getEventsByCameraId(Long cameraId) {
        log.debug("根据摄像头ID查询事件: {}", cameraId);
        
        List<Event> events = eventRepository.findByCameraId(cameraId);
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'active'")
    public List<EventDTO> getActiveEvents() {
        log.debug("查询活跃事件");
        
        List<Event> events = eventRepository.findActiveEvents();
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("根据时间范围查询事件: {} - {}", startTime, endTime);
        
        List<Event> events = eventRepository.findByTimeRange(startTime, endTime);
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Scheduled(fixedDelay = 300000) // 每5分钟执行一次
    @CacheEvict(value = "events", allEntries = true)
    public void autoCloseTimeoutEvents() {
        log.info("开始自动关闭超时事件");
        
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(1); // 1小时前
        List<Event> timeoutEvents = eventRepository.findEventsForAutoClose(thresholdTime);
        
        for (Event event : timeoutEvents) {
            event.setStatus(EventStatus.FALSE_ALARM);
            event.setHandler("系统");
            event.setHandlerNotes("自动关闭：超时未确认");
            event.setUpdatedAt(LocalDateTime.now());
            event.setEndTime(LocalDateTime.now());
            
            eventRepository.save(event);
            log.info("自动关闭超时事件: ID={}, 标题={}", event.getId(), event.getTitle());
        }
        
        log.info("自动关闭超时事件完成，处理数量: {}", timeoutEvents.size());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "statistics", key = "'event_stats'")
    public EventStatistics getEventStatistics() {
        log.debug("获取事件统计信息");
        
        EventStatistics statistics = new EventStatistics();
        statistics.setTotalEvents(eventRepository.count());
        statistics.setPendingEvents(eventRepository.countByStatus(EventStatus.PENDING_CONFIRMATION));
        statistics.setInProgressEvents(eventRepository.countByStatus(EventStatus.IN_PROGRESS));
        statistics.setResolvedEvents(eventRepository.countByStatus(EventStatus.RESOLVED));
        statistics.setClosedEvents(eventRepository.countByStatus(EventStatus.CLOSED));
        statistics.setFalseAlarmEvents(eventRepository.countByStatus(EventStatus.FALSE_ALARM));
        
        return statistics;
    }
}

