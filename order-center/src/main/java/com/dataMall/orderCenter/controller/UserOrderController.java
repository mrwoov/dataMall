package com.dataMall.orderCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.UserOrder;
import com.dataMall.common.enums.OrderStateTypeEnum;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.orderCenter.feign.UserService;
import com.dataMall.orderCenter.service.UserOrderService;
import jakarta.annotation.Resource;
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
    private UserService userService;
    @Resource
    private UserOrderService userOrderService;

    //用户分页查订单
    @PostMapping("/user/page")
    public BaseResponse<IPage<UserOrder>> page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody UserOrder userOrder) {
        Integer accountId = userService.tokenToUid(token);
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
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId);
        return ResultUtils.success(userOrderList);
    }

    //查用户未付款订单
    @GetMapping("user_get_noPay")
    public BaseResponse<List<UserOrder>> getUserNoPayOrder(@RequestHeader("token") String token) {
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, OrderStateTypeEnum.UNPAID.getValue());
        return ResultUtils.success(userOrderList);
    }

    //查用户已购买订单
    @GetMapping("user_get_buy")
    public BaseResponse<List<UserOrder>> getUserBuyOrder(@RequestHeader("token") String token) {
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<UserOrder> userOrderList = userOrderService.getUserOrderList(accountId, OrderStateTypeEnum.TREATED.getValue());
        return ResultUtils.success(userOrderList);
    }

    //检查订单Feign,返回订单实体类
    @GetMapping("/feign/get")
    public UserOrder getOrderFromFeign(@RequestParam("trade_no") String tradeNo) {
        return userOrderService.getOrderByTradeNo(tradeNo);
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

    //Feign更新订单状态
    @GetMapping("/feign/updateOrderState")
    public boolean updateOrderStateFromFeign(@RequestParam("trade_no") String tradeNo, @RequestParam("state") Integer state) {
        return userOrderService.updateOrderState(tradeNo, state);
    }

    //提交订单
    @GetMapping("/submit")
    public BaseResponse<Map<String, String>> submitOrder(@RequestHeader("token") String token,
                                                         @RequestParam(value = "type") String type,
                                                         @RequestParam(value = "goods_id", required = false) Integer goodsId,
                                                         @RequestParam(value = "app_id", required = false) String appId) {
        Integer uid = userService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 分流处理
        if (!"goods".equals(type) && !"app".equals(type)) {
            throw new BusinessException(ErrorCode.FAIL, "type参数错误");
        }
        UserOrder userOrder = new UserOrder();
        if ("goods".equals(type)) {
            if (goodsId == null) {
                throw new BusinessException(ErrorCode.FAIL, "goods_id参数错误");
            }
            userOrder = userOrderService.submitOrderOfGoods(uid, goodsId);
        } else {
            if (appId == null) {
                throw new BusinessException(ErrorCode.FAIL, "app_id参数错误");
            }
            userOrder = userOrderService.submitOrderOfExcelApp(uid, appId);
        }
        if (userOrder == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        Map<String, String> map = new HashMap<>();
        map.put("trade_no", userOrder.getTradeNo());
        return ResultUtils.success(map);
    }

    //支付订单
    @GetMapping("/pay/alipay")
    public String pay(@RequestParam("trade_no") String tradeNo, @RequestParam("return_url") String returnUrl) throws Exception {
        UserOrder userOrder = userOrderService.getOneByOption("trade_no", tradeNo);
        long total = userOrder.getTotalAmount();
        double money = total / 100.0;
        return userOrderService.toPayPage(tradeNo, tradeNo, String.valueOf(money), returnUrl);
    }

    //删除订单
    @GetMapping("/close")
    public BaseResponse<Object> close(@RequestHeader("token") String token, @RequestParam("trade_no") String tradeNo) {
        Integer accountId = userService.tokenToUid(token);
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

