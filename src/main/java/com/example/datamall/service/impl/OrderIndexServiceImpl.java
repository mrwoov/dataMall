package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.OrderIndex;
import com.example.datamall.entity.OrderPay;
import com.example.datamall.mapper.OrderIndexMapper;
import com.example.datamall.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@Service
public class OrderIndexServiceImpl extends ServiceImpl<OrderIndexMapper, OrderIndex> implements OrderIndexService {
    @Resource
    private OrderPayService orderPayService;
    @Resource
    private OrderGoodsService orderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;

    //更新订单表状态
    @Override
    public boolean updateOrderSuccess(Integer orderId, String platTradeNo) {
        OrderIndex orderIndex = getById(orderId);
        if (orderIndex == null) {
            return false;
        }
        OrderPay orderPay = orderPayService.getOneByOption("order_id", orderId);
        if (orderPay == null) {
            return false;
        }
        //更新order_index
        orderIndex.setState(2);
        boolean state_index = updateById(orderIndex);
        //更新order_pay
        orderPay.setState(0);
        orderPay.setPlatTradeNo(platTradeNo);
        boolean state_pay = orderPayService.updateById(orderPay);
        return state_pay & state_index;
    }
}
