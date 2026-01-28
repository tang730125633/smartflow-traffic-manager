package com.smartflow.orchestration.repository;

import com.smartflow.orchestration.entity.TrafficGuidance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通诱导数据访问层
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Repository
public interface TrafficGuidanceRepository extends JpaRepository<TrafficGuidance, Long> {
    
    /**
     * 根据诱导ID查找
     */
    TrafficGuidance findByGuidanceId(String guidanceId);
    
    /**
     * 查找当前有效的诱导信息
     */
    @Query("SELECT tg FROM TrafficGuidance tg WHERE " +
           "tg.enabled = true AND " +
           "tg.validFrom <= :currentTime AND " +
           "(tg.validTo IS NULL OR tg.validTo >= :currentTime)")
    List<TrafficGuidance> findValidGuidance(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 根据类型查找有效诱导信息
     */
    @Query("SELECT tg FROM TrafficGuidance tg WHERE " +
           "tg.guidanceType = :type AND " +
           "tg.enabled = true AND " +
           "tg.validFrom <= :currentTime AND " +
           "(tg.validTo IS NULL OR tg.validTo >= :currentTime)")
    List<TrafficGuidance> findValidGuidanceByType(@Param("type") TrafficGuidance.GuidanceType type, 
                                                 @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 根据优先级查找诱导信息
     */
    List<TrafficGuidance> findByPriorityAndEnabledTrueOrderByCreatedAtDesc(Integer priority);
    
    /**
     * 查找指定区域内的诱导信息
     */
    @Query("SELECT tg FROM TrafficGuidance tg WHERE " +
           "tg.targetArea = :area AND " +
           "tg.enabled = true AND " +
           "tg.validFrom <= :currentTime AND " +
           "(tg.validTo IS NULL OR tg.validTo >= :currentTime)")
    List<TrafficGuidance> findByTargetArea(@Param("area") String area, 
                                         @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 统计各类型诱导信息数量
     */
    @Query("SELECT tg.guidanceType, COUNT(tg) FROM TrafficGuidance tg WHERE tg.enabled = true GROUP BY tg.guidanceType")
    List<Object[]> countByGuidanceType();
}
