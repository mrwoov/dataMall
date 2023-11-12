package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.Goods;
import com.example.datamall.entity.OrderGoods;
import com.example.datamall.entity.OrderIndex;
import com.example.datamall.entity.OrderPay;
import com.example.datamall.service.*;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@RestController
@RequestMapping("/order")
public class OrderIndexController {
    @Resource
    private OrderIndexService orderIndexService;
    @Resource
    private OrderPayService orderPayService;
    @Resource
    private OrderGoodsService orderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;

    //todo:订单管理页面
    //Object转Map
    public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> cla = obj.getClass();
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String keyName = field.getName();
            Object value = field.get(obj);
            if (value == null)
                value = "";
            map.put(keyName, value);
        }
        return map;
    }

    //查询订单交易状态
    @GetMapping("/check")
    public ResultData checkOrder(@RequestParam("trade_no") String tradeNo) {
        OrderPay orderPay = orderPayService.getOneByOption("trade_no", tradeNo);
        //查数据库
        if (orderPay == null) {
            System.out.println(3);
            return ResultData.fail();
        }
        if (orderPay.getState() == 0) {
            return ResultData.success();
        }
        //查支付宝
        try {
            Map<String, Object> res = orderPayService.getTradeState(tradeNo);
            if ("TRADE_SUCCESS".equals(res.get("trade_status"))) {
                //执行更新订单状态的操作：order_id,平台订单号
                Integer orderId = orderPay.getOrderId();
                String platTradeNo = (String) res.get("trade_no");
                orderIndexService.updateOrderSuccess(orderId, platTradeNo);
                return ResultData.success();
            }
            System.out.println(1);
            return ResultData.fail();
        } catch (Exception e) {
            System.out.println(2);
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
        OrderIndex orderIndex = new OrderIndex();
        orderIndex.setAccountId(accountId);
        orderIndexService.save(orderIndex);
        int totalPrice = 0;
        for (Integer i : goodsIds) {
            Goods goods = goodsService.getById(i);
            if (goods == null) {
                return ResultData.fail();
            }
            totalPrice = totalPrice + goods.getPrice();
        }
        //后期开事务处理重复遍历的问题
        for (Integer i : goodsIds) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setGoodsId(i);
            orderGoods.setOrderId(orderIndex.getId());
            orderGoodsService.save(orderGoods);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Random random = new Random();
        String timestamp = dateFormat.format(new Date());
        // 生成四位随机数
        int randomNumber = random.nextInt(10000);
        // 格式化随机数使其为4位
        String formattedRandomNumber = String.format("%04d", randomNumber);
        String tradeNo = timestamp + formattedRandomNumber;
        OrderPay orderPay = new OrderPay();
        orderPay.setPayType("alipay");
        orderPay.setTradeNo(tradeNo);
        orderPay.setOrderId(orderIndex.getId());
        orderPay.setTotalAmount(totalPrice);
        orderPayService.save(orderPay);
        Map<String, String> map = new HashMap<>();
        map.put("trade_no", tradeNo);
        return ResultData.success(map);
    }

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody OrderIndex orderIndex) {
        return ResultData.state(orderIndexService.saveOrUpdate(orderIndex));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return orderIndexService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return orderIndexService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<OrderIndex> findAll() {
        return orderIndexService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<OrderIndex> findOne(@PathVariable Integer id) {
        return orderIndexService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<OrderIndex> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        return orderIndexService.page(new Page<>(pageNum, pageSize));
    }
}

