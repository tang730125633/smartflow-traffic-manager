package com.smartflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * SmartFlow Traffic Manager Gateway Service
 * 
 * 网关服务 - 统一入口、路由转发、负载均衡
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}

