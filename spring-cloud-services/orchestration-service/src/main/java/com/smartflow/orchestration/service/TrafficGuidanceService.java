package com.smartflow.orchestration.service;

import com.smartflow.orchestration.entity.TrafficGuidance;

import java.util.List;
import java.util.Map;

/**
 * 交通诱导服务接口
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
public interface TrafficGuidanceService {
    
    /**
     * 创建诱导信息
     */
    TrafficGuidance createGuidance(TrafficGuidance guidance);
    
    /**
     * 更新诱导信息
     */
    TrafficGuidance updateGuidance(Long guidanceId, TrafficGuidance guidance);
    
    /**
     * 删除诱导信息
     */
    void deleteGuidance(Long guidanceId);
    
    /**
     * 获取所有诱导信息
     */
    List<TrafficGuidance> getAllGuidance();
    
    /**
     * 获取当前有效的诱导信息
     */
    List<TrafficGuidance> getValidGuidance();
    
    /**
     * 根据类型获取诱导信息
     */
    List<TrafficGuidance> getGuidanceByType(TrafficGuidance.GuidanceType type);
    
    /**
     * 根据区域获取诱导信息
     */
    List<TrafficGuidance> getGuidanceByArea(String area);
    
    /**
     * 启用/禁用诱导信息
     */
    void toggleGuidance(Long guidanceId, Boolean enabled);
    
    /**
     * 获取诱导信息统计
     */
    Map<String, Object> getGuidanceStatistics();
    
    /**
     * 批量创建诱导信息
     */
    void batchCreateGuidance(List<TrafficGuidance> guidanceList);
    
    /**
     * 清理过期诱导信息
     */
    void cleanExpiredGuidance();
    
    /**
     * 根据优先级获取诱导信息
     */
    List<TrafficGuidance> getGuidanceByPriority(Integer priority);
    
    /**
     * 更新诱导信息显示次数
     */
    void updateDisplayCount(Long guidanceId);
}
