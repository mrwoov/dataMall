package com.dataMall.orderCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.orderCenter.entity.Goods;
import com.dataMall.orderCenter.entity.GoodsSnapshot;
import com.dataMall.orderCenter.entity.UserOrder;
import com.dataMall.orderCenter.feign.AccountService;
import com.dataMall.orderCenter.feign.GoodsService;
import com.dataMall.orderCenter.service.GoodsSnapshotService;
import com.dataMall.orderCenter.service.UserOrderGoodsService;
import com.dataMall.orderCenter.service.UserOrderService;
import com.dataMall.orderCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户订单表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/order")
public class UserOrderController {
    private final String authPath = "order";
    @Resource
    UserOrderGoodsService userOrderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;
    @Resource
    private UserOrderService userOrderService;
    @Resource
    private GoodsSnapshotService goodsSnapshotService;
    

    //用户下载订单商品的资源
    @GetMapping("/download/{orderId}")
    public ResultData downloadGoodsSource(@PathVariable Integer orderId, @RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.ge("state", 1);
        queryWrapper.eq("id", orderId);
        UserOrder userOrder = userOrderService.getOne(queryWrapper);
        if (userOrder==null){
            System.out.println(1);
            return ResultData.fail();
        }
        //逻辑：查订单是否是token用户的，查订单商品的md5
        List<GoodsSnapshot> goodsSnapshotList = userOrderGoodsService.getOrderGoodsSnapshot(orderId);
        List<String> md5List = new ArrayList<>();
        for (GoodsSnapshot goodsSnapshot:goodsSnapshotList){
            md5List.add(goodsSnapshot.getFileMd5());
        }
        return ResultData.success(md5List);
    }

    //用户分页查订单
    @PostMapping("/user/page")
    public ResultData page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody UserOrder userOrder) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        IPage<UserOrder> page = userOrderService.page(pageSize, pageNum, accountId, userOrder.getTradeNo(), userOrder.getState());
        return ResultData.success(page);
    }

    //查用户全部订单
    @GetMapping("user_get_all")
    public ResultData getUserALlOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        List<UserOrder> userOrderList = userOrderService.list(queryWrapper);
        for (UserOrder userOrder : userOrderList) {
            //查快照
            userOrderService.getOrderGoods(userOrder);
        }
        return ResultData.success(userOrderList);
    }

    //查用户未付款订单
    @GetMapping("user_get_noPay")
    public ResultData getUserNoPayOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.eq("state", 0);
        List<UserOrder> userOrderList = userOrderService.list(queryWrapper);
        for (UserOrder userOrder : userOrderList) {
            //查快照
            userOrderService.getOrderGoods(userOrder);
        }
        return ResultData.success(userOrderList);
    }

    //查用户已购买订单
    @GetMapping("user_get_buy")
    public ResultData getUserBuyOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.gt("state", 1);
        List<UserOrder> userOrderList = userOrderService.list(queryWrapper);
        for (UserOrder userOrder : userOrderList) {
            //查快照
            userOrderService.getOrderGoods(userOrder);
        }
        return ResultData.success(userOrderList);
    }

    //检查订单
    @GetMapping("/check")
    public ResultData checkOrder(@RequestParam("trade_no") String tradeNo) {
        UserOrder userOrder = userOrderService.getOneByOption("trade_no", tradeNo);
        //查数据库
        if (userOrder == null) {
            return ResultData.fail();
        }
        if (userOrder.getState() == 1) {
            return ResultData.success();
        }
        boolean state = userOrderService.checkPayState(tradeNo,userOrder.getId());
        return ResultData.state(state);
    }

    //提交订单
    @PostMapping("/submit")
    public ResultData submitOrder(@RequestHeader("token") String token, @RequestBody List<Integer> goodsIds) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }

        UserOrder userOrder = new UserOrder();
        //计算总价
        int totalPrice = 0;
        for (Integer i : goodsIds) {
            System.out.println(i);
            Integer price = goodsService.getGoodsPrice(i);
            if (price == null) {
                return ResultData.fail();
            }
            totalPrice = totalPrice + price;
        }
        //设置其他参数
        userOrder.setTotalAmount(totalPrice);
        userOrder.setPayType("alipay");
        String tradeNo = userOrderService.createTradeNo();
        userOrder.setTradeNo(tradeNo);
        userOrder.setAccountId(accountId);
        userOrderService.save(userOrder);
        //保存订单及快照
        for (Integer i : goodsIds) {
            boolean state = userOrderGoodsService.saveOrderGoods(i, userOrder.getId());
            if (!state) {
                return ResultData.fail();
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("trade_no", tradeNo);
        return ResultData.success(map);
    }

    //支付订单
    @GetMapping("/pay")
    public String pay(@RequestParam("trade_no") String tradeNo,@RequestParam("return_url")String returnUrl) throws Exception {
        UserOrder userOrder = userOrderService.getOneByOption("trade_no", tradeNo);
        Integer total = userOrder.getTotalAmount();
        double money = total / 100.0;
        return userOrderService.toPayPage(tradeNo, tradeNo, String.valueOf(money),returnUrl);
    }

    //关闭订单
    @GetMapping("/close")
    public ResultData close(@RequestHeader("token")String token,@RequestParam("trade_no") String tradeNo){
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trade_no",tradeNo);
        queryWrapper.eq("account_id",accountId);
        UserOrder userOrder = userOrderService.getOne(queryWrapper);
        if (userOrder==null){
            return ResultData.fail();
        }
        if (userOrder.getState()!=0){
            return ResultData.fail();
        }
        userOrder.setState(-1);
        boolean state = userOrderService.updateById(userOrder);
        return ResultData.state(state);
    }
}

