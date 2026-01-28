package com.smartflow.traffic.ai.client;

import com.smartflow.traffic.ai.dto.VideoDataDTO;
import com.smartflow.traffic.ai.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 数据接入服务Feign客户端降级处理
 * 当数据接入服务不可用时提供降级处理
 */
@Slf4j
@Component
public class DataIngestionClientFallback implements DataIngestionClient {

    @Override
    public ApiResponse<List<VideoDataDTO>> getPendingVideoData(Integer limit) {
        log.error("数据接入服务不可用，无法获取待处理视频数据: limit={}", limit);
        return ApiResponse.error("数据接入服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<Void> updateVideoDataStatus(Long videoId, String status) {
        log.error("数据接入服务不可用，无法更新视频数据状态: videoId={}, status={}", videoId, status);
        return ApiResponse.error("数据接入服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<VideoDataDTO> getVideoDataById(Long videoId) {
        log.error("数据接入服务不可用，无法获取视频数据: videoId={}", videoId);
        return ApiResponse.error("数据接入服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<List<CameraDTO>> getCameras() {
        log.error("数据接入服务不可用，无法获取摄像头列表");
        return ApiResponse.error("数据接入服务暂时不可用，请稍后重试");
    }

    @Override
    public ApiResponse<List<VideoDataDTO>> getVideoDataByCamera(Long cameraId, Integer limit) {
        log.error("数据接入服务不可用，无法根据摄像头获取视频数据: cameraId={}, limit={}", cameraId, limit);
        return ApiResponse.error("数据接入服务暂时不可用，请稍后重试");
    }
}

