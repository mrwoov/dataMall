package com.example.datamall.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.config.AlipayConfig;
import com.example.datamall.entity.OrderPay;
import com.example.datamall.mapper.OrderPayMapper;
import com.example.datamall.service.OrderIndexService;
import com.example.datamall.service.OrderPayService;
import com.example.datamall.utils.JSONUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@Service
public class OrderPayServiceImpl extends ServiceImpl<OrderPayMapper, OrderPay> implements OrderPayService {
    @Autowired
    private AlipayConfig alipayConfig;
    @Resource
    private OrderIndexService orderIndexService;

    /**
     * 电脑版支付
     *
     * @param subject 标题
     * @param orderId 订单ID
     * @param total   金额
     */
    @Override
    public String toPayPage(String subject, String orderId, String total) throws Exception {
        AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, orderId, total, alipayConfig.return_url);
        return response.getBody();
    }

    /**
     * 查询交易状态
     *
     * @param outTradeNo 生成的外部订单号 out_trade_no
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getTradeState(String outTradeNo) throws Exception {
        AlipayTradeQueryResponse query = Factory.Payment.Common().query(outTradeNo);
        Map<String, Object> map = JSONUtils.jsonToMap(query.getHttpBody());

        // 将返回的Object转换为Map后，可以直接使用get方法获取trade_no
        return (Map<String, Object>) map.get("alipay_trade_query_response");
    }

    @Override
    public OrderPay getOneByOption(String column, Object value) {
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }
}