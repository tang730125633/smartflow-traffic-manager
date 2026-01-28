package com.smartflow.traffic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SmartFlow Traffic Manager Traffic Service
 * 
 * 交通数据服务 - 实时视频流处理、目标检测、事故识别
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
@EnableKafka
@EnableAsync
public class TrafficServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficServiceApplication.class, args);
    }
}


