package com.smartflow.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交通数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficDataDTO {
    
    private Long id;
    
    @NotNull(message = "时间不能为空")
    private LocalDateTime dateTime;
    
    private Integer pedestrianCount = 0;
    private Integer carCount = 0;
    private Integer bicycleCount = 0;
    private Integer busCount = 0;
    private Integer motorcycleCount = 0;
    private Integer truckCount = 0;
    
    private BigDecimal pedestrianSpeed = BigDecimal.ZERO;
    private BigDecimal carSpeed = BigDecimal.ZERO;
    private BigDecimal bicycleSpeed = BigDecimal.ZERO;
    private BigDecimal busSpeed = BigDecimal.ZERO;
    private BigDecimal motorcycleSpeed = BigDecimal.ZERO;
    private BigDecimal truckSpeed = BigDecimal.ZERO;
    
    private BigDecimal volume = BigDecimal.ZERO;
    private Integer congestionLevel = 0;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

