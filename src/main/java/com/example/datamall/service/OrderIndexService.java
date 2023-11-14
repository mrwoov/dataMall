package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.OrderIndex;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
public interface OrderIndexService extends IService<OrderIndex> {

    //更新订单表状态
    boolean updateOrderSuccess(Integer orderId, String platTradeNo);

    IPage<OrderIndex> page(Integer pageSize, Integer pageNum, String username, String tradeNo);
}
