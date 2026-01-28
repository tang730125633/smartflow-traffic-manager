package com.smartflow.ai.service;

import com.smartflow.ai.dto.TrafficInfoDto;
import com.smartflow.ai.model.AccidentDetection;
import com.smartflow.ai.model.TrafficData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * AI检测服务接口
 */
public interface AiDetectionService {
    
    /**
     * 开始视频流检测
     */
    void startVideoDetection();
    
    /**
     * 停止视频流检测
     */
    void stopVideoDetection();
    
    /**
     * 处理单帧图像
     */
    TrafficInfoDto processFrame(byte[] imageData);
    
    /**
     * 检测事故
     */
    AccidentDetection detectAccident(byte[] imageData);
    
    /**
     * 获取实时交通数据
     */
    TrafficInfoDto getCurrentTrafficInfo();
    
    /**
     * 获取历史交通数据
     */
    List<TrafficData> getHistoricalTrafficData(int hours);
    
    /**
     * 获取事故检测记录
     */
    List<AccidentDetection> getAccidentDetections(int hours);
    
    /**
     * 更新事故状态
     */
    void updateAccidentStatus(Long accidentId, String status);
}

