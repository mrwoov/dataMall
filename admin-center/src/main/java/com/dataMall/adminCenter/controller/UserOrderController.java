package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.UserOrder;
import com.dataMall.adminCenter.service.UserOrderService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private UserOrderService userOrderService;

    //管理员分页查找
    @PostMapping("/admin/page")
    @AdminAuth(value = authPath)
    public ResultData page(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody UserOrder userOrder) {
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        IPage<UserOrder> page = userOrderService.page(pageSize, pageNum, userOrder.getUsername(), userOrder.getTradeNo());
        return ResultData.success(page);
    }

    //管理员获取订单detail
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData findOne(@PathVariable Integer id) {
        UserOrder userOrder = userOrderService.getById(id);
        userOrderService.getOrderGoods(userOrder);
        return ResultData.success(userOrder);
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


}

