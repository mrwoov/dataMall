package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.adminCenter.entity.GoodsSnapshot;
import com.dataMall.adminCenter.entity.UserOrder;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.service.UserOrderGoodsService;
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
    UserOrderGoodsService userOrderGoodsService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private AccountService accountService;
    @Resource
    private UserOrderService userOrderService;

    //管理员分页查找
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

    //查找单个
    @GetMapping("/{id}")
    public UserOrder findOne(@PathVariable Integer id) {
        return userOrderService.getById(id);
    }

}

