package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.OrderGoods;
import com.example.datamall.service.OrderGoodsService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@RestController
@RequestMapping("/orderGoods")
public class OrderGoodsController {
    @Resource
    private OrderGoodsService orderGoodsService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody OrderGoods orderGoods) {
        return ResultData.state(orderGoodsService.saveOrUpdate(orderGoods));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return orderGoodsService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return orderGoodsService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<OrderGoods> findAll() {
        return orderGoodsService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<OrderGoods> findOne(@PathVariable Integer id) {
        return orderGoodsService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<OrderGoods> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        return orderGoodsService.page(new Page<>(pageNum, pageSize));
    }
}

