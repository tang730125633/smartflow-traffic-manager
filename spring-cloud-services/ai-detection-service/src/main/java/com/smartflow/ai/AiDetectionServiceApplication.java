package com.smartflow.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AI检测服务启动类
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableAsync
public class AiDetectionServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AiDetectionServiceApplication.class, args);
    }
}