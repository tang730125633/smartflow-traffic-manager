package com.smartflow.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * SmartFlow Traffic Manager Frontend Service
 * 
 * 前端服务 - 用户界面、页面渲染、静态资源服务
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
public class FrontendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendServiceApplication.class, args);
    }
}


