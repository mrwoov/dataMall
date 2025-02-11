package com.dataMall.adminCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@RefreshScope
@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.dataMall.common","com.dataMall.adminCenter"})
public class AdminCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminCenterApplication.class, args);
        
    }
}
