package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.GoodsFreeze;
import com.example.datamall.service.GoodsFreezeService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 冻结详解表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-19
 */
@RestController
@RequestMapping("/goodsFreeze")
public class GoodsFreezeController {
    @Resource
    private GoodsFreezeService goodsFreezeService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody GoodsFreeze goodsFreeze) {
        return ResultData.state(goodsFreezeService.saveOrUpdate(goodsFreeze));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return goodsFreezeService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return goodsFreezeService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<GoodsFreeze> findAll() {
        return goodsFreezeService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<GoodsFreeze> findOne(@PathVariable Integer id) {
        return goodsFreezeService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<GoodsFreeze> findPage(@RequestParam Integer pageNum,
                                      @RequestParam Integer pageSize) {
        return goodsFreezeService.page(new Page<>(pageNum, pageSize));
    }
}

