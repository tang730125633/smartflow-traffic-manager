package com.smartflow.ai.repository;

import com.smartflow.ai.entity.AccidentDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事故检测Repository接口
 */
@Repository
public interface AccidentDetectionRepository extends JpaRepository<AccidentDetection, Long> {
    
    /**
     * 根据时间范围查询事故检测数据
     */
    List<AccidentDetection> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据状态查询事故检测数据
     */
    List<AccidentDetection> findByStatusOrderByDateTimeDesc(AccidentDetection.Status status);
    
    /**
     * 根据严重程度查询事故检测数据
     */
    List<AccidentDetection> findBySeverityOrderByDateTimeDesc(AccidentDetection.Severity severity);
    
    /**
     * 查询高置信度的事故检测数据
     */
    @Query("SELECT a FROM AccidentDetection a WHERE a.confidenceScore >= :threshold ORDER BY a.dateTime DESC")
    List<AccidentDetection> findHighConfidenceAccidents(@Param("threshold") Double threshold);
    
    /**
     * 统计指定时间范围内的事故数量
     */
    @Query("SELECT COUNT(a) FROM AccidentDetection a WHERE a.dateTime BETWEEN :startTime AND :endTime")
    Long countAccidentsByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询待处理的事故检测数据
     */
    @Query("SELECT a FROM AccidentDetection a WHERE a.status IN ('PENDING', 'INVESTIGATING') ORDER BY a.dateTime DESC")
    List<AccidentDetection> findPendingAccidents();
}