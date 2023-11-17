package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.goodsCenter.entity.GoodsSnapshot;
import com.dataMall.goodsCenter.service.GoodsSnapshotService;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品快照表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/goodsSnapshot")
public class GoodsSnapshotController {
    @Resource
    private GoodsSnapshotService goodsSnapshotService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody GoodsSnapshot goodsSnapshot) {
        return ResultData.state(goodsSnapshotService.saveOrUpdate(goodsSnapshot));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return goodsSnapshotService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return goodsSnapshotService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<GoodsSnapshot> findAll() {
        return goodsSnapshotService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public GoodsSnapshot findOne(@PathVariable Integer id) {
        return goodsSnapshotService.getById(id);
    }

    //分页查询
    @GetMapping("/page")
    public Page<GoodsSnapshot> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
        return goodsSnapshotService.page(new Page<>(pageNum, pageSize));
    }
}

