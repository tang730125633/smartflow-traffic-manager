package com.smartflow.ai.repository;

import com.smartflow.ai.entity.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通数据Repository接口
 */
@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {
    
    /**
     * 根据时间范围查询交通数据
     */
    List<TrafficData> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询最新的交通数据
     */
    @Query("SELECT t FROM TrafficData t ORDER BY t.dateTime DESC")
    List<TrafficData> findLatestTrafficData();
    
    /**
     * 根据拥堵级别查询交通数据
     */
    List<TrafficData> findByCongestionLevelOrderByDateTimeDesc(Integer congestionLevel);
    
    /**
     * 统计指定时间范围内的平均交通量
     */
    @Query("SELECT AVG(t.volume) FROM TrafficData t WHERE t.dateTime BETWEEN :startTime AND :endTime")
    Double getAverageVolumeByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的平均速度
     */
    @Query("SELECT AVG(t.carSpeed) FROM TrafficData t WHERE t.dateTime BETWEEN :startTime AND :endTime")
    Double getAverageCarSpeedByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
}