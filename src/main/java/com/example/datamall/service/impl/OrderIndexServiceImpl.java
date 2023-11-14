package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Account;
import com.example.datamall.entity.OrderIndex;
import com.example.datamall.entity.OrderPay;
import com.example.datamall.mapper.OrderIndexMapper;
import com.example.datamall.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        orderIndex.setState(1);
        boolean state_index = updateById(orderIndex);
        //更新order_pay
        orderPay.setState(0);
        orderPay.setPlatTradeNo(platTradeNo);
        boolean state_pay = orderPayService.updateById(orderPay);
        return state_pay & state_index;
    }

    @Override
    public IPage<OrderIndex> page(Integer pageSize, Integer pageNum, String username, String tradeNo) {
        QueryWrapper<OrderIndex> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Account> accountQueryWrapper = new QueryWrapper<>();
        QueryWrapper<OrderPay> orderPayQueryWrapper = new QueryWrapper<>();
        if (username != null) {
            accountQueryWrapper.like("username", username);
            List<Account> accountList = accountService.list(accountQueryWrapper);
            List<Integer> accounts = new ArrayList<>();
            for (Account account : accountList) {
                accounts.add(account.getId());
            }
            queryWrapper.in("account_id", accounts);
        }
        if (tradeNo != null) {
            orderPayQueryWrapper.like("trade_no", tradeNo);
            List<OrderPay> orderPayList = orderPayService.list(orderPayQueryWrapper);
            List<Integer> orderPays = new ArrayList<>();
            for (OrderPay orderPay : orderPayList) {
                orderPays.add(orderPay.getOrderId());
            }
            queryWrapper.in("id", orderPays);
        }
        IPage<OrderIndex> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (OrderIndex orderIndex : page.getRecords()) {
            orderIndex.setUsername(accountService.getById(orderIndex.getAccountId()).getUsername());
            OrderPay orderPay = orderPayService.getOneByOption("order_id", orderIndex.getId());
            orderIndex.setTradeNo(orderPay.getTradeNo());
            Integer totalAmount = orderPay.getTotalAmount();
            double money = (double) totalAmount / 100;
            orderIndex.setMoney(money);
        }
        return page;
    }
}
