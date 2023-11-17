package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.GoodsSnapshot;
import com.dataMall.adminCenter.entity.UserOrderGoods;

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
