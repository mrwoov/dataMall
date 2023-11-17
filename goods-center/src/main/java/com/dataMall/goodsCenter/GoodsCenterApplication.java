package com.dataMall.goodsCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@RefreshScope
@SpringBootApplication
@EnableFeignClients
public class GoodsCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsCenterApplication.class,args);
    }
}
