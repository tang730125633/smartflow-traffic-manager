package com.smartflow.traffic.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 事件管理服务启动类
 * 负责交通事件的状态管理和流转
 */
@SpringBootApplication
@EnableCaching
@EnableKafka
@EnableFeignClients
public class EventManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementServiceApplication.class, args);
    }
}

