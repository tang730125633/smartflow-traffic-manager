package com.smartflow.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SmartFlow Traffic Manager Orchestration Service
 * 
 * 调度与诱导服务 - 信号调度、规则引擎、交通诱导
 * 
 * 主要功能:
 * - 交通信号灯智能调度
 * - 规则引擎自动执行
 * - 公交/车辆诱导
 * - 实时交通优化
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
public class OrchestrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationServiceApplication.class, args);
    }
}
