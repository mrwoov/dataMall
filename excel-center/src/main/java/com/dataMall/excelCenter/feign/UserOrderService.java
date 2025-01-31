package com.dataMall.excelCenter.feign;


import com.dataMall.common.entity.UserOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "order-center", path = "/order")
public interface UserOrderService {
    //通过订单号获取订单信息
    @GetMapping("/feign/get")
    UserOrder getOrderFromFeign(@RequestParam("trade_no") String tradeNo);

    //更新订单状态
    @GetMapping("/feign/updateOrderState")
    boolean updateOrderStateFromFeign(@RequestParam("trade_no") String tradeNo, @RequestParam("state") Integer state);

}
