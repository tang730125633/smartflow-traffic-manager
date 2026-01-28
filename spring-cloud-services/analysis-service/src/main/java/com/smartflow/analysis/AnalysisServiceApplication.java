package com.smartflow.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SmartFlow Traffic Manager Analysis Service
 * 
 * 数据分析服务 - 交通数据统计、趋势分析、报告生成
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
@EnableKafka
@EnableScheduling
public class AnalysisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}


