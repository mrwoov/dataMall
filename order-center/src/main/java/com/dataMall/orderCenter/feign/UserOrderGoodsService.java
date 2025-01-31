package com.dataMall.orderCenter.feign;

import com.dataMall.common.entity.Goods;
import com.dataMall.common.entity.GoodsSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "goods-center",path = "/userOrderGoods")
public interface UserOrderGoodsService {
    //Feign，快照是否有商品
    @GetMapping("/snapshotHaveGoods")
    Boolean snapshotHaveGoods(@RequestParam Integer snapshotId, @RequestParam Integer goodsId);
    
    //Feign，保存商品快照
    @GetMapping("/saveGoodsSnapshot")
    Integer saveGoodsSnapshot(@RequestParam Integer goodsId);

    //Feign，根据订单id获取商品快照
    @GetMapping("/isExistSnapshot")
    Integer isExistSnapshot(Goods goods);
    
    //Feign，根据订单id获取商品快照
    @GetMapping("/getOrderGoodsSnapshot")
    List<GoodsSnapshot> getOrderGoodsSnapshot(@RequestParam Integer orderId);

    //Feign，删除商品快照
    @GetMapping("/deleteGoodsSnapshot")
    Boolean deleteGoodsSnapshot(@RequestParam Integer goodsId);

    @GetMapping("/saveOrderGoods")
    Boolean saveOrderGoods(@RequestParam Integer goodsId, @RequestParam Integer orderId);
}
