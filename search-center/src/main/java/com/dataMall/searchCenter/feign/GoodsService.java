package com.dataMall.searchCenter.feign;

import com.dataMall.common.entity.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Component
@FeignClient(value = "goods-center",path = "/goods")
public interface GoodsService {
    
    @GetMapping("/getGoodsListAll")
    List<Goods> getGoodsListAll();
    
    @PostMapping("/getGoodsListByIds")
    List<Goods> getGoodsListByIds(List<Integer> ids);

    @GetMapping("/getGoodsWithFiveMinutesAgoUpdate")
    List<Goods> getGoodsWithFiveMinutesAgoUpdate();
}
