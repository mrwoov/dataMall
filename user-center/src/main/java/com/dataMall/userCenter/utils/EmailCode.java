package com.dataMall.userCenter.utils;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmailCode {
    @Resource
    EmailCode emailCode;
    @Resource
    private RedisUtils redisUtils;

    public void create(String email, String code) {
        redisUtils.set(email, code, 60 * 5);
    }

    public boolean use(String email, String code) {
        String tempCode = redisUtils.get(email);
        boolean state = Objects.equals(tempCode, code);
        if (state) {
            redisUtils.del(email);
        }
        return !state;
    }
}
