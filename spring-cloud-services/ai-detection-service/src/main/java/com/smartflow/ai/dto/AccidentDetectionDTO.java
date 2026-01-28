package com.smartflow.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 事故检测数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentDetectionDTO {
    
    private Long id;
    
    @NotNull(message = "时间不能为空")
    private LocalDateTime dateTime;
    
    private String imageData; // Base64编码的图片数据
    
    private String involvedVehicles;
    
    @NotNull(message = "置信度分数不能为空")
    @DecimalMin(value = "0.0", message = "置信度分数不能小于0")
    @DecimalMax(value = "1.0", message = "置信度分数不能大于1")
    private BigDecimal confidenceScore;
    
    private String severity = "MEDIUM";
    private String status = "PENDING";
    private String location;
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

