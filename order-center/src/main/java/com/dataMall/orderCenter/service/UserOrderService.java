package com.dataMall.orderCenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.orderCenter.entity.UserOrder;

import java.util.List;
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

    UserOrder getOneByOption(String column, Object value);

    //更新订单表状态
    boolean updateOrderSuccess(Integer orderId, String platTradeNo);

    IPage<UserOrder> page(Integer pageSize, Integer pageNum, Integer accountId, String tradeNo,Integer state);

    Map<String, Object> getTradeState(String outTradeNo) throws Exception;


    boolean checkPlatformPayState(String tradeNo, Integer orderId);

    String createTradeNo();

    String toPayPage(String subject, String orderId, String total,String returnUrl) throws Exception;

    void getOrderGoods(UserOrder userOrder);

    List<UserOrder> getUserOrderList(Integer accountId, Integer state);

    List<UserOrder> getUserOrderList(Integer accountId);

    boolean checkOrderPayState(String tradeNo);

    boolean deleteOrder(String tradeNo,Integer accountId);

    UserOrder getUserPayedOrderByTradeNo(String tradeNo, Integer accountId);

    List<String> downloadByMd5List(Integer orderId);
}
