package com.smartflow.orchestration.repository;

import com.smartflow.orchestration.entity.TrafficSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 交通信号灯数据访问层
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Repository
public interface TrafficSignalRepository extends JpaRepository<TrafficSignal, Long> {
    
    /**
     * 根据信号灯ID查找
     */
    Optional<TrafficSignal> findBySignalId(String signalId);
    
    /**
     * 根据路口ID查找所有信号灯
     */
    List<TrafficSignal> findByIntersectionId(String intersectionId);
    
    /**
     * 查找启用的信号灯
     */
    List<TrafficSignal> findByEnabledTrue();
    
    /**
     * 查找自动调度的信号灯
     */
    List<TrafficSignal> findByAutoSchedulingTrue();
    
    /**
     * 根据状态查找信号灯
     */
    List<TrafficSignal> findByCurrentStatus(TrafficSignal.SignalStatus status);
    
    /**
     * 查找指定区域内的信号灯
     */
    @Query("SELECT ts FROM TrafficSignal ts WHERE " +
           "ts.longitude BETWEEN :minLng AND :maxLng AND " +
           "ts.latitude BETWEEN :minLat AND :maxLat")
    List<TrafficSignal> findByLocationRange(@Param("minLng") Double minLng, 
                                          @Param("maxLng") Double maxLng,
                                          @Param("minLat") Double minLat, 
                                          @Param("maxLat") Double maxLat);
    
    /**
     * 统计各状态信号灯数量
     */
    @Query("SELECT ts.currentStatus, COUNT(ts) FROM TrafficSignal ts GROUP BY ts.currentStatus")
    List<Object[]> countByStatus();
}
