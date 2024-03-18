package com.dataMall.orderCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.orderCenter.entity.Account;
import com.dataMall.orderCenter.entity.UserOrder;
import com.dataMall.orderCenter.feign.AccountService;
import com.dataMall.orderCenter.feign.GoodsService;
import com.dataMall.orderCenter.service.UserOrderGoodsService;
import com.dataMall.orderCenter.service.UserOrderService;
import com.dataMall.orderCenter.utils.MailService;
import com.dataMall.orderCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    UserOrderGoodsService userOrderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;
    @Resource
    private UserOrderService userOrderService;
    @Autowired
    private MailService mailService;

    //用户下载订单商品的资源
    @GetMapping("/download/{tradeNo}")
    public ResultData downloadGoodsSource(@PathVariable String tradeNo, @RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        UserOrder userOrder = userOrderService.getUserPayedOrderByTradeNo(tradeNo, accountId);
        if (userOrder == null) {
            return ResultData.fail();
        }
        List<String> md5List = userOrderService.downloadByMd5List(userOrder.getId());
        return ResultData.success(md5List);
    }

    //发送下载链接
    @GetMapping("/sendDownload/{tradeNo}")
    public ResultData sendDownload( @PathVariable String tradeNo,@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        UserOrder userOrder = userOrderService.getUserPayedOrderByTradeNo(tradeNo, accountId);
        if (userOrder == null) {
            return ResultData.fail();
        }
        List<String> md5List = userOrderService.downloadByMd5List(userOrder.getId());
        if (md5List.isEmpty()) {
            return ResultData.fail("没有资源");
        }
        Account account = accountService.getById(accountId);
        if (account == null) {
            return ResultData.fail("用户不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("tradeNo", tradeNo);
        map.put("md5List", md5List);
        String html = "";
        int i = 1;
        for (String md5 : md5List) {
            html = html + "<p>资源" + i + "：" + md5 + "</p> <a href='http://localhost:8080/order/download/" + tradeNo + "'>点击下载</a>";
            i++;
        }
        mailService.sendTextMailMessage(account.getEmail(), "资源下载链接", "资源下载链接：" +html);
        return ResultData.success();
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
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId);
        return ResultData.success(userOrderList);
    }

    //查用户未付款订单
    @GetMapping("user_get_noPay")
    public ResultData getUserNoPayOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, 0);
        return ResultData.success(userOrderList);
    }

    //查用户已购买订单
    @GetMapping("user_get_buy")
    public ResultData getUserBuyOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, 1);
        return ResultData.success(userOrderList);
    }

    //检查订单
    @GetMapping("/check")
    public ResultData checkOrder(@RequestParam("trade_no") String tradeNo) {
        boolean state = userOrderService.checkOrderPayState(tradeNo);
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
    public String pay(@RequestParam("trade_no") String tradeNo, @RequestParam("return_url") String returnUrl) throws Exception {
        UserOrder userOrder = userOrderService.getOneByOption("trade_no", tradeNo);
        Integer total = userOrder.getTotalAmount();
        double money = total / 100.0;
        return userOrderService.toPayPage(tradeNo, tradeNo, String.valueOf(money), returnUrl);
    }

    //删除订单
    @GetMapping("/close")
    public ResultData close(@RequestHeader("token") String token, @RequestParam("trade_no") String tradeNo) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        boolean state = userOrderService.deleteOrder(tradeNo, accountId);
        return ResultData.state(state);
    }
}

