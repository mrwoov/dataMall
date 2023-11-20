package com.dataMall.orderCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.orderCenter.entity.GoodsSnapshot;
import com.dataMall.orderCenter.entity.UserOrderGoods;

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

    boolean orderHaveGoods(Integer orderId, Integer goodsId);

    boolean saveOrderGoods(Integer goodsId, Integer orderId);

    List<GoodsSnapshot> getOrderGoodsSnapshot(Integer orderId);
}
