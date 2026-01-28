package com.smartflow.traffic.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * AI检测服务启动类
 * 基于深度学习的交通事件检测服务
 */
@SpringBootApplication
@EnableCaching
@EnableKafka
@EnableFeignClients
public class AiDetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDetectionServiceApplication.class, args);
    }
}

