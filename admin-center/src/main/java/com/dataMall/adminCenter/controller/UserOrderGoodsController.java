package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.adminCenter.service.UserOrderGoodsService;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.UserOrderGoods;
import com.dataMall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单商品表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/userOrderGoods")
public class UserOrderGoodsController {
    @Resource
    private UserOrderGoodsService userOrderGoodsService;

    //新增或修改
    @PatchMapping("/")
    public BaseResponse<Object> saveOrUpdate(@RequestBody UserOrderGoods userOrderGoods) {
        boolean state = userOrderGoodsService.saveOrUpdate(userOrderGoods);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userOrderGoodsService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return userOrderGoodsService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<UserOrderGoods> findAll() {
        return userOrderGoodsService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public UserOrderGoods findOne(@PathVariable Integer id) {
        return userOrderGoodsService.getById(id);
    }

    //分页查询
    @GetMapping("/page")
    public Page<UserOrderGoods> findPage(@RequestParam Integer pageNum,
                                         @RequestParam Integer pageSize) {
        return userOrderGoodsService.page(new Page<>(pageNum, pageSize));
    }
}

