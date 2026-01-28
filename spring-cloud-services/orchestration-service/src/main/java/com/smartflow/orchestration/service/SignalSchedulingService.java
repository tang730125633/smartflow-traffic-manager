package com.smartflow.orchestration.service;

import com.smartflow.orchestration.entity.TrafficSignal;
import com.smartflow.orchestration.entity.SchedulingRule;

import java.util.List;
import java.util.Map;

/**
 * 信号调度服务接口
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
public interface SignalSchedulingService {
    
    /**
     * 获取所有信号灯状态
     */
    List<TrafficSignal> getAllSignals();
    
    /**
     * 根据ID获取信号灯
     */
    TrafficSignal getSignalById(String signalId);
    
    /**
     * 更新信号灯状态
     */
    TrafficSignal updateSignalStatus(String signalId, TrafficSignal.SignalStatus status, Integer remainingTime);
    
    /**
     * 执行信号调度
     */
    void executeScheduling();
    
    /**
     * 根据交通流量调整信号灯
     */
    void adjustSignalByTrafficVolume(String signalId, Integer trafficVolume);
    
    /**
     * 根据事件调整信号灯
     */
    void adjustSignalByIncident(String signalId, String incidentType, Integer severity);
    
    /**
     * 执行紧急调度
     */
    void executeEmergencyScheduling(String signalId);
    
    /**
     * 获取信号灯统计信息
     */
    Map<String, Object> getSignalStatistics();
    
    /**
     * 批量更新信号灯
     */
    void batchUpdateSignals(List<TrafficSignal> signals);
    
    /**
     * 启用/禁用自动调度
     */
    void toggleAutoScheduling(String signalId, Boolean enabled);
    
    /**
     * 获取调度规则
     */
    List<SchedulingRule> getSchedulingRules();
    
    /**
     * 执行调度规则
     */
    void executeSchedulingRule(Long ruleId);
}
