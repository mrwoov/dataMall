package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.UserOrderGoods;
import com.example.datamall.service.UserOrderGoodsService;
import com.example.datamall.vo.ResultData;
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
    public ResultData saveOrUpdate(@RequestBody UserOrderGoods userOrderGoods) {
        return ResultData.state(userOrderGoodsService.saveOrUpdate(userOrderGoods));
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

