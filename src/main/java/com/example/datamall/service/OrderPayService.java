package com.example.datamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.OrderPay;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
public interface OrderPayService extends IService<OrderPay> {

    String toPayPage(String subject, String orderId, String total) throws Exception;

    Map<String, Object> getTradeState(String outTradeNo) throws Exception;

    OrderPay getOneByOption(String column, Object value);
}
