package com.example.datamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.GoodsSnapshot;
import com.example.datamall.entity.UserOrderGoods;

import java.util.List;

/**
 * <p>
 * 订单商品表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
public interface UserOrderGoodsService extends IService<UserOrderGoods> {

    boolean saveOrderGoods(Integer goodsId, Integer orderId);

    List<GoodsSnapshot> getOrderGoodsSnapshot(Integer orderId);
}
