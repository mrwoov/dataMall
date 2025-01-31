package com.dataMall.orderCenter.feign;


import com.dataMall.common.entity.ExcelApp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Component
@FeignClient(value = "excel-center", path = "/excelApp")
public interface ExcelAppService {
    @GetMapping("/feign/getExcelApp")
    ExcelApp getExcelApp(@RequestParam("appId") String appId);
}
