package com.dataMall.apiCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@RefreshScope
@SpringBootApplication
@EnableFeignClients
public class ApiCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiCenterApplication.class, args);
    }

}
