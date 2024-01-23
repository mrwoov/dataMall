package com.dataMall.orderCenter.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.orderCenter.config.AlipayConfig;
import com.dataMall.orderCenter.entity.GoodsSnapshot;
import com.dataMall.orderCenter.entity.UserOrder;
import com.dataMall.orderCenter.feign.AccountService;
import com.dataMall.orderCenter.mapper.UserOrderMapper;
import com.dataMall.orderCenter.service.UserOrderGoodsService;
import com.dataMall.orderCenter.service.UserOrderService;
import com.dataMall.orderCenter.utils.JSONUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    @Autowired
    private AmqpTemplate amqpTemplate;

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
    public IPage<UserOrder> page(Integer pageSize, Integer pageNum, Integer accountId, String tradeNo, Integer state) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        if (state!=null){
            queryWrapper.eq("state",state);
        }
        if (tradeNo != null) {
            queryWrapper.like("trade_no", tradeNo);
        }
        queryWrapper.orderByDesc("id");
        IPage<UserOrder> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (UserOrder userOrder : page.getRecords()) {
            userOrder.setUsername(accountService.getById(userOrder.getAccountId()).getUsername());
            double money = (double) userOrder.getTotalAmount() / 100;
            userOrder.setMoney(money);
            userOrder.setGoodsSnapshots(userOrderGoodsService.getOrderGoodsSnapshot(userOrder.getId()));
        }
        return page;
    }

    /**
     * 查询支付宝交易状态
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
    public boolean checkPlatformPayState(String tradeNo, Integer orderId){
        //查支付宝
        try {
            Map<String, Object> res = getTradeState(tradeNo);
            if ("TRADE_SUCCESS".equals(res.get("trade_status"))) {
                //执行更新订单状态的操作：order_id,平台订单号
                String platTradeNo = (String) res.get("trade_no");
                return updateOrderSuccess(orderId, platTradeNo);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
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
    public String toPayPage(String subject, String tradeNo, String total,String returnUrl) throws Exception {
        this.sendDelayedMessage(tradeNo);
        AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, tradeNo, total, returnUrl);
        return response.getBody();
    }

    //延迟队列处理
    @RabbitListener(queues = "delay_queue")
    public void handleOrderEvent(@Payload String tradeNo) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trade_no", tradeNo);
        UserOrder userOrder = getOne(queryWrapper);
        if (userOrder.getState()!=0){
            return;
        }
        boolean payState = checkPlatformPayState(tradeNo,userOrder.getId());
        if (payState){
            return;
        }
        LocalDateTime orderCreationTime = userOrder.getCreateTime();
        // 计算时间差
        long minutesElapsed = ChronoUnit.MINUTES.between(orderCreationTime, LocalDateTime.now());
        if (minutesElapsed>=15){
            // 如果超过15分钟，则触发订单关闭操作
            closeOrderByServe(userOrder);
        }
        
    }
    //发送延迟消息到延迟交换机
    public void sendDelayedMessage(String orderId) {
        long delay = 15*60*1000;
        this.amqpTemplate.convertAndSend("delayed_exchange", "delay_key", orderId, message -> {
            message.getMessageProperties().setDelay((int) delay);
            return message;
        });
    }
    public void closeOrderByServe(UserOrder userOrder){
        userOrder.setState(-1);
        updateById(userOrder);
    }
    @Override
    public void getOrderGoods(UserOrder userOrder) {
        List<GoodsSnapshot> goodsSnapshotList = userOrderGoodsService.getOrderGoodsSnapshot(userOrder.getId());
        userOrder.setGoodsSnapshots(goodsSnapshotList);
        userOrder.setUsername(accountService.getById(userOrder.getAccountId()).getUsername());
        userOrder.setTradeNo(userOrder.getTradeNo());
        Integer totalAmount = userOrder.getTotalAmount();
        double money = (double) totalAmount / 100;
        userOrder.setMoney(money);
    }

    @Override
    public List<UserOrder> getUserOrderList(Integer accountId) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);

        List<UserOrder> userOrderList = list(queryWrapper);
        for (UserOrder userOrder : userOrderList) {
            //查快照
            getOrderGoods(userOrder);
        }
        return userOrderList;
    }

    @Override
    public List<UserOrder> getUserOrderList(Integer accountId, Integer state) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        if (state==0){
            queryWrapper.eq("state", state);
        }
        else {
            queryWrapper.gt("state", state);
        }
        List<UserOrder> userOrderList = list(queryWrapper);
        for (UserOrder userOrder : userOrderList) {
            //查快照
            getOrderGoods(userOrder);
        }
        return userOrderList;
    }

    @Override
    public boolean checkOrderPayState(String tradeNo){
        UserOrder userOrder = getOneByOption("trade_no", tradeNo);
        //查数据库
        if (userOrder == null) {
            return false;
        }
        if (userOrder.getState() == 1) {
            return true;
        }
        return checkPlatformPayState(tradeNo,userOrder.getId());
    }
}