package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.goodsCenter.entity.GoodsPortalShow;
import com.dataMall.goodsCenter.service.GoodsPortalShowService;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/goodsPortalShow")
    public class GoodsPortalShowController {
@Resource
private GoodsPortalShowService goodsPortalShowService;

//新增或修改
@PatchMapping("/")
public ResultData saveOrUpdate(@RequestBody GoodsPortalShow goodsPortalShow){
        return ResultData.state(goodsPortalShowService.saveOrUpdate(goodsPortalShow));
        }
//删除by id
@DeleteMapping("/{id}")
public Boolean delete(@PathVariable Integer id){
        return goodsPortalShowService.removeById(id);
        }
//批量删除
@PostMapping("/del_batch")
public Boolean deleteBatch(@RequestBody List<Integer> ids){
        return goodsPortalShowService.removeByIds(ids);
        }
//查找全部
@GetMapping
public List<GoodsPortalShow> findAll(){
        return goodsPortalShowService.list();
        }
//查找单个
@GetMapping("/{id}")
public List<GoodsPortalShow> findOne(@PathVariable Integer id){
        return goodsPortalShowService.list();
        }
//分页查询
@GetMapping("/page")
public Page<GoodsPortalShow> findPage(@RequestParam Integer pageNum,
@RequestParam Integer pageSize){
        return goodsPortalShowService.page(new Page<>(pageNum,pageSize));
        }
        }

