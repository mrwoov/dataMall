package com.dataMall.orderCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Account;
import com.dataMall.common.entity.UserOrder;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.orderCenter.feign.AccountService;
import com.dataMall.orderCenter.feign.GoodsService;
import com.dataMall.orderCenter.service.UserOrderGoodsService;
import com.dataMall.orderCenter.service.UserOrderService;
import com.dataMall.orderCenter.utils.MailService;
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
    public BaseResponse<List<String>> downloadGoodsSource(@PathVariable String tradeNo, @RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
           throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        UserOrder userOrder = userOrderService.getUserPayedOrderByTradeNo(tradeNo, accountId);
        if (userOrder == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        List<String> md5List = userOrderService.downloadByMd5List(userOrder.getId());
        return ResultUtils.success(md5List);
    }

    //发送下载链接
    @GetMapping("/sendDownload/{tradeNo}")
    public BaseResponse<Object> sendDownload(@PathVariable String tradeNo, @RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        UserOrder userOrder = userOrderService.getUserPayedOrderByTradeNo(tradeNo, accountId);
        if (userOrder == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        List<String> md5List = userOrderService.downloadByMd5List(userOrder.getId());
        if (md5List.isEmpty()) {
            throw new BusinessException(ErrorCode.FAIL, "没有资源");
        }
        Account account = accountService.getById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.FAIL, "用户不存在");
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
        return ResultUtils.success();
    }

    //用户分页查订单
    @PostMapping("/user/page")
    public BaseResponse<IPage<UserOrder>> page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody UserOrder userOrder) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IPage<UserOrder> page = userOrderService.page(pageSize, pageNum, accountId, userOrder.getTradeNo(), userOrder.getState());
        return ResultUtils.success(page);
    }

    //查用户全部订单
    @GetMapping("user_get_all")
    public BaseResponse<List<UserOrder>> getUserALlOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId);
        return ResultUtils.success(userOrderList);
    }

    //查用户未付款订单
    @GetMapping("user_get_noPay")
    public BaseResponse<List<UserOrder>> getUserNoPayOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, 0);
        return ResultUtils.success(userOrderList);
    }

    //查用户已购买订单
    @GetMapping("user_get_buy")
    public BaseResponse<List<UserOrder>> getUserBuyOrder(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, 1);
        return ResultUtils.success(userOrderList);
    }

    //检查订单
    @GetMapping("/check")
    public BaseResponse<Object> checkOrder(@RequestParam("trade_no") String tradeNo) {
        boolean state = userOrderService.checkOrderPayState(tradeNo);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //提交订单
    @PostMapping("/submit")
    public BaseResponse<Map<String, String>> submitOrder(@RequestHeader("token") String token, @RequestBody List<Integer> goodsIds) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        UserOrder userOrder = new UserOrder();
        //计算总价
        int totalPrice = 0;
        for (Integer i : goodsIds) {
            Integer price = goodsService.getGoodsPrice(i);
            if (price == null) {
                throw new BusinessException(ErrorCode.FAIL);
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
               throw new BusinessException(ErrorCode.FAIL);
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("trade_no", tradeNo);
        return ResultUtils.success(map);
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
    public BaseResponse<Object> close(@RequestHeader("token") String token, @RequestParam("trade_no") String tradeNo) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean state = userOrderService.deleteOrder(tradeNo, accountId);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }
}

