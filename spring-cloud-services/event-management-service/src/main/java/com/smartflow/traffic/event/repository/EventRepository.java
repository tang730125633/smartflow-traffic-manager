package com.smartflow.traffic.event.repository;

import com.smartflow.traffic.event.entity.Event;
import com.smartflow.traffic.event.entity.EventStatus;
import com.smartflow.traffic.event.entity.EventLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 事件Repository接口
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 根据检测ID查找事件
     */
    Optional<Event> findByDetectionId(Long detectionId);

    /**
     * 根据状态查找事件
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * 根据级别查找事件
     */
    List<Event> findByLevel(EventLevel level);

    /**
     * 根据摄像头ID查找事件
     */
    List<Event> findByCameraId(Long cameraId);

    /**
     * 根据状态和级别查找事件
     */
    List<Event> findByStatusAndLevel(EventStatus status, EventLevel level);

    /**
     * 查找指定时间范围内的事件
     */
    @Query("SELECT e FROM Event e WHERE e.startTime BETWEEN :startTime AND :endTime")
    List<Event> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查找指定状态和时间范围内的事件
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startTime BETWEEN :startTime AND :endTime")
    List<Event> findByStatusAndTimeRange(@Param("status") EventStatus status,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查找未关闭的事件
     */
    @Query("SELECT e FROM Event e WHERE e.status NOT IN ('CLOSED', 'FALSE_ALARM', 'CANCELLED')")
    List<Event> findActiveEvents();

    /**
     * 根据处理人员查找事件
     */
    List<Event> findByHandler(String handler);

    /**
     * 统计指定状态的事件数量
     */
    long countByStatus(EventStatus status);

    /**
     * 统计指定级别的事件数量
     */
    long countByLevel(EventLevel level);

    /**
     * 查找需要自动关闭的事件（超过指定时间未更新的待确认事件）
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'PENDING_CONFIRMATION' AND e.createdAt < :thresholdTime")
    List<Event> findEventsForAutoClose(@Param("thresholdTime") LocalDateTime thresholdTime);
}

