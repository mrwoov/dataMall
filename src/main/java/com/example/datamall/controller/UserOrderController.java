package com.example.datamall.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.Goods;
import com.example.datamall.entity.GoodsSnapshot;
import com.example.datamall.entity.UserOrder;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsService;
import com.example.datamall.service.UserOrderGoodsService;
import com.example.datamall.service.UserOrderService;
import com.example.datamall.vo.ResultData;
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
    private final String authPath = "order";
    @Resource
    UserOrderGoodsService userOrderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;
    @Resource
    private UserOrderService userOrderService;

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
        //查支付宝
        try {
            Map<String, Object> res = userOrderService.getTradeState(tradeNo);
            if ("TRADE_SUCCESS".equals(res.get("trade_status"))) {
                //执行更新订单状态的操作：order_id,平台订单号
                Integer orderId = userOrder.getId();
                String platTradeNo = (String) res.get("trade_no");
                boolean state = userOrderService.updateOrderSuccess(orderId, platTradeNo);
                return ResultData.state(state);
            }
            return ResultData.fail();
        } catch (Exception e) {
            return ResultData.fail();
        }
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
            Goods goods = goodsService.getById(i);
            if (goods == null) {
                return ResultData.fail();
            }
            totalPrice = totalPrice + goods.getPrice();
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

    //分页查找
    @PostMapping("/admin/page")
    public ResultData page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody UserOrder userOrder) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        IPage<UserOrder> page = userOrderService.page(pageSize, pageNum, userOrder.getUsername(), userOrder.getTradeNo());
        return ResultData.success(page);
    }

    //管理员获取订单detail
    @GetMapping("/admin/{id}")
    public ResultData findOne(@RequestHeader("token") String token, @PathVariable Integer id) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        UserOrder userOrder = userOrderService.getById(id);
        List<GoodsSnapshot> goodsSnapshotList = userOrderGoodsService.getOrderGoodsSnapshot(id);
        userOrder.setGoodsSnapshots(goodsSnapshotList);
        userOrder.setUsername(accountService.getById(userOrder.getAccountId()).getUsername());
        userOrder.setTradeNo(userOrder.getTradeNo());
        Integer totalAmount = userOrder.getTotalAmount();
        double money = (double) totalAmount / 100;
        userOrder.setMoney(money);
        return ResultData.success(userOrder);
    }

    @GetMapping("/pay")
    public String pay(@RequestParam("trade_no") String tradeNo) throws Exception {
        UserOrder userOrder = userOrderService.getOneByOption("trade_no", tradeNo);
        Integer total = userOrder.getTotalAmount();
        double money = total / 100.0;
        return userOrderService.toPayPage(tradeNo, tradeNo, String.valueOf(money));
    }
    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody UserOrder userOrder) {
        return ResultData.state(userOrderService.saveOrUpdate(userOrder));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userOrderService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return userOrderService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<UserOrder> findAll() {
        return userOrderService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public UserOrder findOne(@PathVariable Integer id) {
        return userOrderService.getById(id);
    }

    //分页查询
    @GetMapping("/page")
    public Page<UserOrder> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        return userOrderService.page(new Page<>(pageNum, pageSize));
    }
}

