package com.dataMall.userCenter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "admin-center",path = "/admins")
public interface AdminFeign {
    @GetMapping("/isAdmin")
    boolean isAdmin(@RequestParam("accountId") Integer accountId);
}
