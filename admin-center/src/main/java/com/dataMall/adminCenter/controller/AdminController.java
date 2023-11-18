package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public ResultData panelInfo(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
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

        return ResultData.success(res);
    }

    //管理员新增或修改管理员
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestHeader("token") String token, @RequestBody Admin admin) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        if (admin.getAccountId() == null) {
            admin.setAccountId(accountService.getOneByOption("username", admin.getUsername()).getId());
        }
        if (admin.getRole() == null) {
            admin.setRole(roleService.getOneByOption("roleName", admin.getRoleName()).getId());
        }
        return ResultData.state(adminService.saveOrUpdate(admin));
    }

    @DeleteMapping("/{id}")
    //管理员删除管理员
    public ResultData del(@RequestHeader("token") String token, @PathVariable Integer id) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(adminService.removeById(id));
    }

    //管理员批量删除管理员
    @PostMapping("/del_batch")
    public ResultData delBatch(@RequestHeader("token") String token, @RequestBody List<Integer> ids) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(adminService.removeBatchByIds(ids));
    }

    //分页查询查询管理员
    @PostMapping("/query")
    public ResultData query(@RequestHeader("token") String token, @RequestBody Admin admin, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (!admin.getUsername().isBlank()) {
            Integer uid = accountService.getOneByOption("username", admin.getUsername()).getId();
            queryWrapper.eq("account_id", uid);
        }
        if (admin.getRole() != null) {
            queryWrapper.eq("role", admin.getRole());
        }
        IPage<Admin> page = adminService.page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Admin admin1 : page.getRecords()) {
            admin1.setUsername(accountService.getById(admin1.getAccountId()).getUsername());
            admin1.setRoleName(roleService.getById(admin1.getRole()).getRoleName());
        }
        return ResultData.success(page);
    }
}

