package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.common.BaseResponse;
import com.dataMall.adminCenter.common.ErrorCode;
import com.dataMall.adminCenter.common.ResultUtils;
import com.dataMall.adminCenter.entity.Admin;
import com.dataMall.adminCenter.exception.BusinessException;
import com.dataMall.adminCenter.service.*;
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
    public BaseResponse<Map<String, String>> panelInfo() {
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
        return ResultUtils.success(res);
    }

    //管理员新增或修改管理员
    @PatchMapping("/")
    @AdminAuth(value = authPath)
    public BaseResponse saveOrUpdate(@RequestBody Admin admin) {
        if (admin.getAccountId() == null) {
            admin.setAccountId(accountService.getOneByOption("username", admin.getUsername()).getId());
        }
        if (admin.getRole() == null) {
            admin.setRole(roleService.getOneByOption("roleName", admin.getRoleName()).getId());
        }
        boolean state = adminService.saveOrUpdate(admin);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    @DeleteMapping("/{id}")
    @AdminAuth(value = authPath)
    //管理员删除管理员
    public BaseResponse<Object> del(@PathVariable Integer id) {
        boolean state = adminService.removeById(id);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员批量删除管理员
    @PostMapping("/del_batch")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delBatch(@RequestBody List<Integer> ids) {
        boolean state = adminService.removeBatchByIds(ids);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //分页查询查询管理员
    @PostMapping("/query")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<Admin>> query(@RequestBody Admin admin, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        IPage<Admin> page = adminService.query(admin.getUsername(), admin.getRole(), pageNum, pageSize);
        return ResultUtils.success(page);
    }

    //是否是管理员
    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("accountId") Integer accountId) {
        return adminService.isAdmin(accountId);
    }
}

