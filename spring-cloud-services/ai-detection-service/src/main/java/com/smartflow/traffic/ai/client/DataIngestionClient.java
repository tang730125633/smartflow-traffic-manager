package com.smartflow.traffic.ai.client;

import com.smartflow.traffic.ai.dto.VideoDataDTO;
import com.smartflow.traffic.ai.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据接入服务Feign客户端
 * 用于调用数据接入服务的API
 */
@FeignClient(name = "data-ingestion-service", fallback = DataIngestionClientFallback.class)
public interface DataIngestionClient {

    /**
     * 获取待处理的视频数据
     * @param limit 限制数量
     * @return 视频数据列表
     */
    @GetMapping("/api/video-data/pending")
    ApiResponse<List<VideoDataDTO>> getPendingVideoData(@RequestParam(value = "limit", defaultValue = "10") Integer limit);

    /**
     * 更新视频数据处理状态
     * @param videoId 视频ID
     * @param status 处理状态
     * @return 更新结果
     */
    @PutMapping("/api/video-data/{videoId}/status")
    ApiResponse<Void> updateVideoDataStatus(@PathVariable("videoId") Long videoId, 
                                          @RequestParam("status") String status);

    /**
     * 根据ID获取视频数据
     * @param videoId 视频ID
     * @return 视频数据
     */
    @GetMapping("/api/video-data/{videoId}")
    ApiResponse<VideoDataDTO> getVideoDataById(@PathVariable("videoId") Long videoId);

    /**
     * 获取摄像头列表
     * @return 摄像头列表
     */
    @GetMapping("/api/cameras")
    ApiResponse<List<CameraDTO>> getCameras();

    /**
     * 根据摄像头ID获取视频数据
     * @param cameraId 摄像头ID
     * @param limit 限制数量
     * @return 视频数据列表
     */
    @GetMapping("/api/video-data/by-camera/{cameraId}")
    ApiResponse<List<VideoDataDTO>> getVideoDataByCamera(@PathVariable("cameraId") Long cameraId,
                                                        @RequestParam(value = "limit", defaultValue = "10") Integer limit);
}

/**
 * 摄像头DTO
 */
class CameraDTO {
    private Long id;
    private String name;
    private String location;
    private String ipAddress;
    private Integer port;
    private String status;
    private String description;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

