package com.example.datamall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.GoodsFile;
import com.example.datamall.service.GoodsFileService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品数据文件 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
@RestController
@RequestMapping("/goodsFile")
public class GoodsFileController {
    @Resource
    private GoodsFileService goodsFileService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody GoodsFile goodsFile) {
        return ResultData.state(goodsFileService.saveOrUpdate(goodsFile));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return goodsFileService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return goodsFileService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<GoodsFile> findAll() {
        return goodsFileService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<GoodsFile> findOne(@PathVariable Integer id) {
        return goodsFileService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<GoodsFile> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        return goodsFileService.page(new Page<>(pageNum, pageSize));
    }
}

