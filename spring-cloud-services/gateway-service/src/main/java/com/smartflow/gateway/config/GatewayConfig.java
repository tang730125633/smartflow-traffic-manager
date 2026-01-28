package com.smartflow.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置
 * 
 * 配置各个微服务的路由规则
 */
@Configuration
public class GatewayConfig {

    /**
     * 配置路由规则
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://auth-service"))
                
                // 交通数据服务路由
                .route("traffic-service", r -> r
                        .path("/api/traffic/**")
                        .uri("lb://traffic-service"))
                
                // 数据分析服务路由
                .route("analysis-service", r -> r
                        .path("/api/analysis/**")
                        .uri("lb://analysis-service"))
                
                // 通知服务路由
                .route("notification-service", r -> r
                        .path("/api/notification/**")
                        .uri("lb://notification-service"))
                
                // 前端服务路由
                .route("frontend-service", r -> r
                        .path("/**")
                        .uri("lb://frontend-service"))
                
                .build();
    }
}

