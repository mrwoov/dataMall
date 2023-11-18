package com.dataMall.orderCenter.feign;

import com.dataMall.orderCenter.entity.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Component
@FeignClient(value = "goods-center",path = "/goods")
public interface GoodsService {
    @GetMapping("/getById/{id}")
    Goods getById(@PathVariable Integer id);

    @GetMapping("/getGoodsPrice/{id}")
    Integer getGoodsPrice(@PathVariable Integer id);
}
