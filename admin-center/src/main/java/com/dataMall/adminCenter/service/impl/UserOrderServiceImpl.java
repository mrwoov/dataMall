package com.dataMall.adminCenter.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.config.AlipayConfig;
import com.dataMall.adminCenter.entity.Account;
import com.dataMall.adminCenter.entity.GoodsSnapshot;
import com.dataMall.adminCenter.entity.UserOrder;
import com.dataMall.adminCenter.mapper.UserOrderMapper;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.UserOrderGoodsService;
import com.dataMall.adminCenter.service.UserOrderService;
import com.dataMall.adminCenter.utils.JSONUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 用户订单表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {
    @Resource
    private AlipayConfig alipayConfig;
    @Resource
    private AccountService accountService;
    @Resource
    private UserOrderGoodsService userOrderGoodsService;

    @Override
    //获取今日订单数
    public long getTodayOrderCount() {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0), java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999));
        return count(queryWrapper);
    }
    @Override
    //获取今日销售总额
    public double getTodayMoney() {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0), java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999));
        queryWrapper.ge("state", 1);
        List<UserOrder> userOrderList = list(queryWrapper);
        Integer total = 0;
        for (UserOrder userOrder : userOrderList) {
            total += userOrder.getTotalAmount();
        }
        return (double) total / 100;
    }
    @Override
    public double getYesterdayMoney(){
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time",java.time.LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0),java.time.LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999));
        queryWrapper.ge("state", 1);
        List<UserOrder> userOrderList = list(queryWrapper);
        Integer total = 0;
        for (UserOrder userOrder : userOrderList) {
            total += userOrder.getTotalAmount();
        }
        return (double) total / 100;
    }

    @Override
    public UserOrder getOneByOption(String column, Object value) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    //更新订单表状态
    @Override
    public boolean updateOrderSuccess(Integer orderId, String platTradeNo) {
        UserOrder userOrder = getById(orderId);
        if (userOrder == null) {
            return false;
        }
        //更新order_index
        userOrder.setPayTime(LocalDateTime.now());
        userOrder.setState(1);
        userOrder.setPlatTradeNo(platTradeNo);
        return updateById(userOrder);
    }

    @Override
    public IPage<UserOrder> page(Integer pageSize, Integer pageNum, String username, String tradeNo) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Account> accountQueryWrapper = new QueryWrapper<>();

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
            queryWrapper.like("trade_no", tradeNo);
        }
        IPage<UserOrder> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (UserOrder userOrder : page.getRecords()) {
            userOrder.setUsername(accountService.getById(userOrder.getAccountId()).getUsername());
            double money = (double) userOrder.getTotalAmount() / 100;
            userOrder.setMoney(money);
        }
        return page;
    }

    /**
     * 查询交易状态
     *
     * @param outTradeNo 生成的外部订单号 out_trade_no
     */
    @Override
    public Map<String, Object> getTradeState(String outTradeNo) throws Exception {
        AlipayTradeQueryResponse query = Factory.Payment.Common().query(outTradeNo);
        Map<String, Object> map = JSONUtils.jsonToMap(query.getHttpBody());

        // 将返回的Object转换为Map后，可以直接使用get方法获取trade_no
        return (Map<String, Object>) map.get("alipay_trade_query_response");
    }

    @Override
    public String createTradeNo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Random random = new Random();
        String timestamp = dateFormat.format(new Date());
        // 生成四位随机数
        int randomNumber = random.nextInt(10000);
        // 格式化随机数使其为4位
        String formattedRandomNumber = String.format("%04d", randomNumber);
        return timestamp + formattedRandomNumber;
    }

    @Override
    public String toPayPage(String subject, String orderId, String total) throws Exception {
        AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, orderId, total, alipayConfig.return_url);
        return response.getBody();
    }

    @Override
    public UserOrder getOrderGoods(UserOrder userOrder){
        List<GoodsSnapshot> goodsSnapshotList = userOrderGoodsService.getOrderGoodsSnapshot(userOrder.getId());
        userOrder.setGoodsSnapshots(goodsSnapshotList);
        userOrder.setUsername(accountService.getById(userOrder.getAccountId()).getUsername());
        userOrder.setTradeNo(userOrder.getTradeNo());
        Integer totalAmount = userOrder.getTotalAmount();
        double money = (double) totalAmount / 100;
        userOrder.setMoney(money);
        return userOrder;
    }
}
