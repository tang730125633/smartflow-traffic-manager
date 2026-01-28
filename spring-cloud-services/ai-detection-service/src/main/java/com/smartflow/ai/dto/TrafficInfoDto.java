package com.smartflow.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 交通信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficInfoDto {
    
    private Map<String, VehicleInfo> vehicles;
    private Double totalVolume;
    private Integer congestionLevel;
    private String congestionLevelText;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleInfo {
        private Integer count;
        private Double averageSpeed;
    }
}

