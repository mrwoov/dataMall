package com.dataMall.adminCenter.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {
    @Value("${aliyun.oss.endpoint}")
    public String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    public String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    public String accessKeySecret;

    @Bean
    public OSS oss() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
