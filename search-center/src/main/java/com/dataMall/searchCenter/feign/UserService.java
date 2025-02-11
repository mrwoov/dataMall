package com.dataMall.searchCenter.feign;

import com.dataMall.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
@FeignClient(value = "user-center",path = "/users")
public interface UserService {
    @GetMapping("/getUserList")
    List<User> getUserList();
}
