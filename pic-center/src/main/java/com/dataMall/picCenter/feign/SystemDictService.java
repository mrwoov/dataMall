package com.dataMall.picCenter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "system-center",path = "/systemDict")
public interface SystemDictService {
    @GetMapping("/admin/getSetting")
    String getSetting(@RequestParam("key") String key);
}
