package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.OrderPay;
import com.example.datamall.service.OrderPayService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@RestController
@RequestMapping("/orderPay")
public class OrderPayController {
    @Resource
    private OrderPayService orderPayService;

    @GetMapping("/pay")
    public String pay(@RequestParam("trade_no") String tradeNo) throws Exception {
        OrderPay orderPay = orderPayService.getOneByOption("trade_no", tradeNo);
        Integer total = orderPay.getTotalAmount();
        double money = total / 100.0;
        System.out.println();
        return orderPayService.toPayPage(tradeNo, tradeNo, String.valueOf(money));
    }

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody OrderPay orderPay) {
        return ResultData.state(orderPayService.saveOrUpdate(orderPay));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return orderPayService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return orderPayService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<OrderPay> findAll() {
        return orderPayService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<OrderPay> findOne(@PathVariable Integer id) {
        return orderPayService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<OrderPay> findPage(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize) {
        return orderPayService.page(new Page<>(pageNum, pageSize));
    }
}

