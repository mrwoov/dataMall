package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Admin;
import com.dataMall.adminCenter.service.*;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@RestController
@RequestMapping("/admins")
public class AdminController {
    private final String authPath = "admins";
    @Resource
    private AccountService accountService;
    @Resource
    private AdminService adminService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserOrderService userOrderService;
    @Resource
    private GoodsService goodsService;

    @GetMapping("/panel")
    @AdminAuth(value = authPath)
    public ResultData panelInfo() {
        Map<String, String> res = new HashMap<>();
        //订单数据
        res.put("order_today_num", String.valueOf(userOrderService.getTodayOrderCount()));
        res.put("order_today_money", String.valueOf(userOrderService.getTodayMoney()));
        res.put("order_yesterday_money", String.valueOf(userOrderService.getYesterdayMoney()));
        //商品数据
        res.put("goods_total", String.valueOf(goodsService.getGoodsCount()));
        res.put("goods_not_audit", String.valueOf(goodsService.getNotAuditGoodsCount()));
        res.put("goods_normal", String.valueOf(goodsService.getNormalGoodsCount()));
        //用户数据
        res.put("user_today_num", String.valueOf(accountService.getTodayNewUserCount()));
        res.put("user_yesterday_num", String.valueOf(accountService.getYesterdayNewUserCount()));
        res.put("user_total_num", String.valueOf(accountService.getUserTotal()));
        res.put("user_month_num", String.valueOf(accountService.getThisMonthNewUserCount()));
        return ResultData.success(res);
    }

    //管理员新增或修改管理员
    @PatchMapping("/")
    @AdminAuth(value = authPath)
    public ResultData saveOrUpdate( @RequestBody Admin admin) {
        if (admin.getAccountId() == null) {
            admin.setAccountId(accountService.getOneByOption("username", admin.getUsername()).getId());
        }
        if (admin.getRole() == null) {
            admin.setRole(roleService.getOneByOption("roleName", admin.getRoleName()).getId());
        }
        return ResultData.state(adminService.saveOrUpdate(admin));
    }

    @DeleteMapping("/{id}")
    @AdminAuth(value = authPath)
    //管理员删除管理员
    public ResultData del( @PathVariable Integer id) {
        return ResultData.state(adminService.removeById(id));
    }

    //管理员批量删除管理员
    @PostMapping("/del_batch")
    @AdminAuth(value = authPath)
    public ResultData delBatch( @RequestBody List<Integer> ids) {
        return ResultData.state(adminService.removeBatchByIds(ids));
    }

    //分页查询查询管理员
    @PostMapping("/query")
    @AdminAuth(value = authPath)
    public ResultData query( @RequestBody Admin admin, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        IPage<Admin> page = adminService.query(admin.getUsername(), admin.getRole(), pageNum, pageSize);
        return ResultData.success(page);
    }

    //是否是管理员
    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("accountId") Integer accountId) {
        return adminService.isAdmin(accountId);
    }
}

