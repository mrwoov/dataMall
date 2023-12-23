package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.UserOrder;

import java.util.Map;

/**
 * <p>
 * 用户订单表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
public interface UserOrderService extends IService<UserOrder> {

    //获取今日订单数
    long getTodayOrderCount();

    //获取今日销售总额
    double getTodayMoney();

    double getYesterdayMoney();

    UserOrder getOneByOption(String column, Object value);

    //更新订单表状态
    boolean updateOrderSuccess(Integer orderId, String platTradeNo);

    IPage<UserOrder> page(Integer pageSize, Integer pageNum, String username, String tradeNo);

    Map<String, Object> getTradeState(String outTradeNo) throws Exception;

    String createTradeNo();

    String toPayPage(String subject, String orderId, String total) throws Exception;

    UserOrder getOrderGoods(UserOrder userOrder);
}
