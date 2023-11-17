package com.dataMall.goodsCenter.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCode {
    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    EmailCode emailCode;

    public void create(String email, String code) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(email, code, 60 * 5, TimeUnit.SECONDS);
    }

    public boolean use(String email, String code) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String tempCode = operations.get(email);
        boolean state = Objects.equals(tempCode, code);
        if (state) {
            redisTemplate.delete(email);
        }
        return !state;
    }
}
